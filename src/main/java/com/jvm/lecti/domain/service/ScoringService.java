package com.jvm.lecti.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jvm.lecti.domain.dao.AppleDAO;
import com.jvm.lecti.domain.dao.PlayerDAO;
import com.jvm.lecti.domain.dao.ResultDAO;
import com.jvm.lecti.domain.entity.Apple;
import com.jvm.lecti.domain.entity.Player;
import com.jvm.lecti.domain.entity.Result;
import com.jvm.lecti.domain.entity.ResultId;
import com.jvm.lecti.domain.enums.ECrownScore;
import com.jvm.lecti.domain.exceptions.ApplePlayerNotFoundException;
import com.jvm.lecti.domain.exceptions.InvalidErrorQuantityException;


@Service("ScoringService")
public class ScoringService {

   private final Integer AT_LEAST_THREE_ERRORS = 3;

   private final Integer AT_LEAST_ONE_ERROR = 1;

   private final Integer ZERO_ERROR = 0;

   private final Integer FIVE_ERRORS = 5;

   @Autowired
   private ResultDAO resultDAO;

   @Autowired
   private PlayerDAO playerDAO;

   @Autowired
   private AppleDAO appleDAO;

   public Integer generateScoreForPlayer(Integer playerId, Integer appleId, List<Integer> exercises)
         throws ApplePlayerNotFoundException, InvalidErrorQuantityException {
      validateExercises(exercises);

      Integer finalScore = calculateTotalScore(exercises);

      saveResult(playerId, appleId, finalScore);

      return finalScore;
   }

   private void validateExercises(List<Integer> exercises) throws InvalidErrorQuantityException {
      if (exercises.stream().anyMatch(errors -> errors < ZERO_ERROR || errors > FIVE_ERRORS)) {
         throw new InvalidErrorQuantityException();
      }
   }

   private Integer calculateTotalScore(List<Integer> exercises) {
      return exercises.stream().map(this::getScoreFromErrors).mapToInt(Integer::intValue).sum() / exercises.size();
   }

   private Integer getScoreFromErrors(Integer errors) {
      if (errors <= AT_LEAST_ONE_ERROR) {
         return ECrownScore.THREE_CROWN.getValue();
      } else if (errors <= AT_LEAST_THREE_ERRORS) {
         return ECrownScore.TWO_CROWN.getValue();
      } else {
         return ECrownScore.ONE_CROWN.getValue();
      }
   }

   private void saveResult(Integer playerId, Integer appleId, Integer finalScore) throws ApplePlayerNotFoundException {
      Optional<Player> player = playerDAO.findById(Long.valueOf(playerId));
      Optional<Apple> apple = appleDAO.findById(appleId);

      if (player.isPresent() && apple.isPresent()) {
         Result result = createResult(player.get(), apple.get(), finalScore);
         resultDAO.save(result);
      } else {
         throw new ApplePlayerNotFoundException();
      }
   }

   private Result createResult(Player player, Apple apple, Integer finalScore) {
      Result result = new Result();
      ResultId resultId = new ResultId();
      resultId.setApple_id(Long.valueOf(apple.getId()));
      resultId.setPlayer_id(player.getId());
      result.setId(resultId);
      result.setPlayer(player);
      result.setApple(apple);
      result.setScore(finalScore);
      return result;
   }

}

