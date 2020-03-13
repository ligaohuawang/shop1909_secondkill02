package com.qf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class User extends BaseEntity implements Serializable {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String nickname;


}
