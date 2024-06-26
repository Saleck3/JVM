package com.jvm.lecti.presentation.dto.response;

import com.jvm.lecti.domain.enums.AppleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppleDto {

   private Integer id;

   private String name;

   private Integer score;

   private AppleType appleType;

}
