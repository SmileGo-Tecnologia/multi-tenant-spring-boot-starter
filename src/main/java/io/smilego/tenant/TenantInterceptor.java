package io.smilego.tenant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

public class TenantInterceptor implements WebRequestInterceptor {

    @Value("${multitenancy.tenant-header:X-TENANT-ID}")
    private String tenantHeader;

    @Override
    public void preHandle(WebRequest webRequest) throws Exception {
        String tenantId = null;
        if (webRequest.getHeader(tenantHeader) != null) {
            tenantId = webRequest.getHeader("X-TENANT-ID");
        } else {
            tenantId = ((ServletWebRequest)webRequest).getRequest().getServerName().split("\\.")[0];
        }
        TenantContext.setTenantId(tenantId);
    }

    @Override
    public void postHandle(WebRequest webRequest, ModelMap modelMap) throws Exception {
        TenantContext.clear();
    }

    @Override
    public void afterCompletion(WebRequest webRequest, Exception e) throws Exception {

    }
}
