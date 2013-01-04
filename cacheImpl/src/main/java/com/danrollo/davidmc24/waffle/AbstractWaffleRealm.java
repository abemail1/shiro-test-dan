package com.danrollo.davidmc24.waffle;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashingPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import com.sun.jna.platform.win32.Win32Exception;

/**
 * A {@link org.apache.shiro.realm.Realm} that authenticates with Active
 * Directory using WAFFLE. Authorization is left for subclasses to define by
 * implementing the {@link #buildAuthorizationInfo} method.
 */
public abstract class AbstractWaffleRealm extends AuthorizingRealm {
    private static final Logger log = LoggerFactory.getLogger(AbstractWaffleRealm.class);
    private static final String realmName = "WAFFLE";

    private final IWindowsAuthProvider provider = new WindowsAuthProviderImpl();

    @Override
    protected final AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken)
            throws AuthenticationException {
        AuthenticationInfo authenticationInfo = null;
        if (!(authToken instanceof UsernamePasswordToken)) {
            return null;
        }
        UsernamePasswordToken token = (UsernamePasswordToken) authToken;
        String username = token.getUsername();
        IWindowsIdentity identity = null;
        try {
            log.debug("Attempting login for user {}", username);
            identity = provider.logonUser(username, new String(token.getPassword()));
            if (identity.isGuest()) {
                log.debug("Guest identity for user {}; denying access", username);
                throw new AuthenticationException("Guest identities are not allowed access");
            } else {
                Object principal = new WaffleFqnPrincipal(identity);
                authenticationInfo = buildAuthenticationInfo(token, principal);
                log.debug("Successful login for user {}", username);
            }
        } catch (Win32Exception ex) {
            log.debug("Failed login for user {}", username, ex);
            throw new AuthenticationException("Login failed", ex);
        } finally {
            if (identity != null) {
                identity.dispose();
            }
        }
        return authenticationInfo;
    }

    private AuthenticationInfo buildAuthenticationInfo(UsernamePasswordToken token, Object principal) {
        AuthenticationInfo authenticationInfo;
        HashingPasswordService hashService = getHashService();
        if (hashService != null) {
            Hash hash = hashService.hashPassword(token.getPassword());
            ByteSource salt = hash.getSalt();
            authenticationInfo = new SimpleAuthenticationInfo(principal, hash, salt, realmName);
        } else {
            Object creds = token.getCredentials();
            authenticationInfo = new SimpleAuthenticationInfo(principal, creds, realmName);
        }
        return authenticationInfo;
    }

    @Override
    protected final AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        WaffleFqnPrincipal principal = principals.oneByType(WaffleFqnPrincipal.class);
        if (principal != null) {
            return buildAuthorizationInfo(principal);
        } else {
            return null;
        }
    }

    /**
     * Assembles the appropriate authorization information for the specified
     * principal.
     *
     * @param principal the principal for which to assemble authorization
     *        information
     * @return the authorization information for the specified principal
     */
    protected abstract AuthorizationInfo buildAuthorizationInfo(WaffleFqnPrincipal principal);

    private HashingPasswordService getHashService() {
        CredentialsMatcher matcher = getCredentialsMatcher();
        if (matcher instanceof PasswordMatcher) {
            PasswordMatcher passwordMatcher = (PasswordMatcher) matcher;
            PasswordService passwordService = passwordMatcher.getPasswordService();
            if (passwordService instanceof HashingPasswordService) {
                return (HashingPasswordService) passwordService;
            }
        }
        return null;
    }
}