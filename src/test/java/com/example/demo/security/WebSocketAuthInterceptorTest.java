//package com.example.demo.security;
//
//import com.example.demo.service.JwtService;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mockito;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.web.socket.WebSocketHandler;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class WebSocketAuthInterceptorTest {
//
//    @MockBean
//    private JwtService jwtService;
//
//    @MockBean
//    private UserDetailsService userDetailsService;
//
//    @InjectMocks
//    private WebSocketAuthInterceptor authInterceptor;
//
//    @Test
//    public void testBeforeHandshake_ValidToken() {
//        // Mock the request with an Authorization header
//        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
//        ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);
//        WebSocketHandler handler = Mockito.mock(WebSocketHandler.class);
//
//        Map<String, Object> attributes = new HashMap<>();
//        Mockito.when(request.getHeaders()).thenReturn(HttpHeaders.writableHttpHeaders(HttpHeaders.EMPTY));
//        Mockito.when(request.getHeaders().getFirst("Authorization")).thenReturn("Bearer validToken");
//
//        // Mock JwtService behavior
//        Mockito.when(jwtService.extractUsername("validToken")).thenReturn("user123");
//        Mockito.when(jwtService.isTokenValid(Mockito.anyString(), Mockito.any())).thenReturn(true);
//
//        // Call the method and verify
//        boolean result = authInterceptor.beforeHandshake(request, response, handler, attributes);
//        assertTrue(result);
//        assertEquals("user123", attributes.get("username"));
//    }
//
//
//