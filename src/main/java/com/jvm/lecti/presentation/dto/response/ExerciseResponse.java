package com.jvm.lecti.presentation.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseResponse {

   private List<ExerciseDto> exercises;

}
