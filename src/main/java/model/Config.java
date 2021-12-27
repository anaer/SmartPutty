package model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Config {
   List<CustomMenu> menus; 
   Map<String, Object> configuration;
   Map<String, String> program;
   Map<String, Boolean> feature;
}
