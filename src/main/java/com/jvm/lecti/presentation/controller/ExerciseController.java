package com.jvm.lecti.presentation.controller;

import static com.jvm.lecti.domain.enums.AppleType.RECOMMENDED_MODULE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jvm.lecti.domain.annotation.CheckPermission;
import com.jvm.lecti.domain.exceptions.ApplePlayerNotFoundException;
import com.jvm.lecti.domain.exceptions.InvalidErrorQuantityException;
import com.jvm.lecti.domain.service.ExerciseService;
import com.jvm.lecti.domain.service.ModuleService;
import com.jvm.lecti.domain.service.ScoringService;
import com.jvm.lecti.presentation.dto.request.ScoreRequest;
import com.jvm.lecti.presentation.dto.response.ErrorResponse;
import com.jvm.lecti.presentation.dto.response.ExerciseDto;
import com.jvm.lecti.presentation.dto.response.ExerciseResponse;
import com.jvm.lecti.domain.entity.Exercise;
import com.jvm.lecti.presentation.dto.response.ScoreResponse;
import com.jvm.lecti.presentation.mappers.ExerciseMapper;
import com.jvm.lecti.presentation.util.ErrorResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@RestController
@RequestMapping("/api/exercise")
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseController {

   @Autowired
   private ExerciseService exerciseService;

   @Autowired
   private ModuleService moduleService;

   @Autowired
   private ScoringService scoringService;

   @CheckPermission
   @GetMapping("/getExerciseByAppleId")
   public ResponseEntity<ExerciseResponse> getExerciseByAppleId(HttpServletRequest httpServletRequest,
         @NonNull @RequestParam(value = "appleId") Integer appleId, @NonNull @RequestParam(value = "playerId") Integer playerId) {
      List<Exercise> exercises = exerciseService.getExercisesByApple(appleId);
      List<ExerciseDto> exercisesDto = ExerciseMapper.INSTANCE.exerciseListToExerciseListDto(exercises);
      Integer moduleId = moduleService.obtainModuleIdFromExercise(exercises);

      return ResponseEntity.ok(ExerciseResponse.builder().moduleId(moduleId).exercises(exercisesDto).build());
   }

   @CheckPermission
   @PostMapping("/obtainScore")
   public ResponseEntity<ScoreResponse> generateScoreForPlayer(HttpServletRequest httpServletRequest, @Valid @RequestBody ScoreRequest scoreRequest) {
      try {
         Integer finalScore = scoringService.generateScoreForPlayer(scoreRequest.getPlayerId(), scoreRequest.getAppleId(),
               scoreRequest.getExercises());
         ScoreResponse scoreResponse = ScoreResponse.builder().score(finalScore).build();
         return ResponseEntity.ok(scoreResponse);
      } catch (ApplePlayerNotFoundException | InvalidErrorQuantityException ex) {
         //Avisar
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ScoreResponse.builder().errorMessage(ex.getMessage()).build());
      }
   }

   @GetMapping("/obtainTest")
   public ResponseEntity<ExerciseResponse> getExercisesRecommendationTest() {
      List<Exercise> exercises = exerciseService.getExercisesByAppleType(RECOMMENDED_MODULE);
      List<ExerciseDto> exercisesDto = ExerciseMapper.INSTANCE.exerciseListToExerciseListDto(exercises);
      return ResponseEntity.ok(ExerciseResponse.builder().exercises(exercisesDto).build());
   }

}
