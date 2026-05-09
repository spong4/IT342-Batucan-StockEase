package edu.cit.batucan.StockEase.feature.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String role;
}
