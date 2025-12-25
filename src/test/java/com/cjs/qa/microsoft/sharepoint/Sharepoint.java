// package com.cjs.qa.microsoft.sharepoint;
//
// import java.io.IOException;
// import java.net.URISyntaxException;
// import java.util.List;
//
// import javax.xml.transform.Transformer;
// import javax.xml.transform.TransformerException;
// import javax.xml.transform.TransformerFactory;
// import javax.xml.transform.dom.DOMResult;
//
// import org.apache.commons.collections.CollectionUtils;
// import org.apache.commons.httpclient.methods.RequestEntity;
// import org.apache.commons.lang.StringUtils;
// import org.eclipse.jetty.http.HttpMethod;
// import org.json.JSONException;
// import org.json.JSONObject;
// import org.w3c.dom.Document;
//
// public class Sharepoint
// {
// public String getFormDigestValue(List<String> cookies)
// throws IOException, URISyntaxException, TransformerException, JSONException
// {
// MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
// headers.add("Cookie", Joiner.on(';').join(cookies));
// headers.add("Accept", "application/json;odata=verbose");
// headers.add("X-ClientService-ClientTag", "SDK-JAVA");
// RequestEntity<String> requestEntity = new RequestEntity<>(headers,
// HttpMethod.POST,
// new URI("[SharePoint domain address]/_api/contextinfo"));
// ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity,
// String.class);
// JSONObject json = new JSONObject(responseEntity.getBody());
// return
// json.getJSONObject("d").getJSONObject("GetContextWebInformation").getString("FormDigestValue");
// }
//
// public List<String> getSignInCookies(String securityToken) throws
// TransformerException, URISyntaxException
// {
// RequestEntity<String> requestEntity = new RequestEntity<>(securityToken,
// HttpMethod.POST,
// new URI("[SharePoint domain address]/_forms/default.aspx?wa=wsignin1.0"));
// ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity,
// String.class);
// HttpHeaders headers = responseEntity.getHeaders();
// List<String> cookies = headers.get("Set-Cookie");
// if (CollectionUtils.isEmpty(cookies))
// {
// throw new SharePointSignInException("Unable to sign in: no cookies returned
// in response");
// }
// return cookies;
// }
//
// public String receiveSecurityToken() throws TransformerException,
// URISyntaxException
// {
// RequestEntity<String> requestEntity = new
// RequestEntity<>(buildSecurityTokenRequestEnvelope(), HttpMethod.POST,
// new URI("https://login.microsoftonline.com/extSTS.srf"));
// ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity,
// String.class);
// DOMResult result = new DOMResult();
// Transformer transformer = TransformerFactory.newInstance().newTransformer();
// transformer.transform(new StringSource(responseEntity.getBody()), result);
// Document definitionDocument = (Document) result.getNode();
// String securityToken = xPathExpression.evaluateAsString(definitionDocument);
// if (StringUtils.isBlank(securityToken))
// {
// throw new SharePointAuthenticationException("Unable to authenticate: empty
// token");
// }
// return securityToken;
// }
// }
