package waffle.shiro.dynamic;

import junit.framework.TestCase;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Dan Rollo
 * Date: 2/26/13
 * Time: 5:47 PM
 */
public class DynamicAuthenticationFilterTest extends TestCase {

    private DynamicAuthenticationFilter dynamicAuthenticationFilter;

    private MockServletRequest request;


    protected void setUp() {
        dynamicAuthenticationFilter = new DynamicAuthenticationFilter();

        request = new MockServletRequest();
    }


    public void testIsAuthTypeNegotiate() {
        assertFalse(dynamicAuthenticationFilter.isAuthTypeNegotiate(request));

        request.parameters.put(DynamicAuthenticationFilter.PARAM_NAME_AUTHTYPE, "zzz");
        assertFalse(dynamicAuthenticationFilter.isAuthTypeNegotiate(request));

        request.parameters.put(DynamicAuthenticationFilter.PARAM_NAME_AUTHTYPE,
                DynamicAuthenticationFilter.PARAM_VAL_AUTHTYPE_NEGOTIATE);
        assertTrue(dynamicAuthenticationFilter.isAuthTypeNegotiate(request));
    }


    private static class MockServletRequest implements ServletRequest {
        private void notImplemented() {
            throw new RuntimeException("not implemented");
        }


        @Override
        public Object getAttribute(String name) { notImplemented(); return null; }

        @Override
        public Enumeration getAttributeNames() { notImplemented(); return null; }

        @Override
        public String getCharacterEncoding() { notImplemented(); return null; }

        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException { notImplemented(); }

        @Override
        public int getContentLength() { notImplemented(); return 0; }

        @Override
        public String getContentType() { notImplemented(); return null; }

        @Override
        public ServletInputStream getInputStream() throws IOException { notImplemented(); return null; }

        Map<String, String> parameters = new HashMap<String, String>();
        @Override
        public String getParameter(String name) {
            return parameters.get(name);
        }

        @Override
        public Enumeration getParameterNames() { notImplemented(); return null; }

        @Override
        public String[] getParameterValues(String name) { notImplemented(); return new String[0]; }

        @Override
        public Map getParameterMap() { notImplemented(); return null; }

        @Override
        public String getProtocol() { notImplemented(); return null; }

        @Override
        public String getScheme() { notImplemented(); return null; }

        @Override
        public String getServerName() { notImplemented(); return null; }

        @Override
        public int getServerPort() { notImplemented(); return 0; }

        @Override
        public BufferedReader getReader() throws IOException { notImplemented(); return null; }

        @Override
        public String getRemoteAddr() { notImplemented(); return null; }

        @Override
        public String getRemoteHost() { notImplemented(); return null; }

        @Override
        public void setAttribute(String name, Object o) { notImplemented(); }

        @Override
        public void removeAttribute(String name) { notImplemented(); }

        @Override
        public Locale getLocale() { notImplemented(); return null; }

        @Override
        public Enumeration getLocales() { notImplemented(); return null; }

        @Override
        public boolean isSecure() { notImplemented(); return false; }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) { notImplemented(); return null; }

        @Override
        public String getRealPath(String path) { notImplemented(); return null; }

        @Override
        public int getRemotePort() { notImplemented(); return 0; }

        @Override
        public String getLocalName() { notImplemented(); return null; }

        @Override
        public String getLocalAddr() { notImplemented(); return null; }

        @Override
        public int getLocalPort() { notImplemented(); return 0; }
    }
}
