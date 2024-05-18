package com.jvm.lecti.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.jsonwebtoken.Claims;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import com.jvm.lecti.dto.response.AppleResponse;
import com.jvm.lecti.entity.Apple;
import com.jvm.lecti.entity.User;
import com.jvm.lecti.exceptions.InvalidUserIdForPlayerException;
import com.jvm.lecti.service.AppleService;
import com.jvm.lecti.service.PlayerService;
import com.jvm.lecti.util.TokenUtil;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
public class AppleControllerTest {

   private final String TOKEN_HEADER = "Authorization";

   private AppleController appleController;

   private AppleService appleService;

   private HttpServletRequest request;

   private TokenUtil tokenUtil;

   private PlayerService playerService;

   @Before
   public void init() {
      appleService = mock(AppleService.class);
      request = mock(HttpServletRequest.class);
      tokenUtil = mock(TokenUtil.class);
      playerService = mock(PlayerService.class);
      appleController = new AppleController(appleService, tokenUtil, playerService);
   }

   @Test
   public void ifIdDoNotExistReturnErrorResponse() {
      Integer playerId = 1;
      Integer appleId = 0;
      givenApple(playerId, appleId);
      ResponseEntity appleRsp = whenAskingForApple(playerId, appleId);
      assertEquals(404, appleRsp.getStatusCode().value());
   }

   @Test
   public void ifAppleIdExistReturnCorrectResponse() {
      Integer playerId = 1;
      Integer appleId = 1;
      givenApple(playerId, appleId);
      ResponseEntity appleRsp = whenAskingForApple(playerId, appleId);
      assertEquals(200, appleRsp.getStatusCode().value());
   }

   private ResponseEntity whenAskingForApple(int playerId, int appleId) {
      return appleController.getApple(request, playerId, appleId);
   }

   private void givenApple(int playerId, int appleId) {
      if (appleId == 0) {
         when(appleService.getApple(appleId, playerId)).thenReturn(null);
      } else {
         Optional<Apple> apple = Optional.of(new Apple());
         apple.get().setId(appleId);
         when(appleService.getApple(appleId, playerId)).thenReturn(apple);
      }
      when(request.getHeader(TOKEN_HEADER)).thenReturn(tokenUtil.createToken(new User()));
   }

}


