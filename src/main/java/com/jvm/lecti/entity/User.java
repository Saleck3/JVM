package com.jvm.lecti.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

   private String email;

   private String password;

   private String firstName;

   private String lastName;

   public User(String email, String password) {
      this.email = email;
      this.password = password;
   }

}
