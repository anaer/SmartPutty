package model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Config {
   /**
    * Application菜单配置.
    */
   List<CustomMenu> menus; 
   /**
    * 应用配置项.
    */
   Map<String, Object> configuration;
   /**
    * 程序路径配置.
    */
   Map<String, String> program;
   /**
    * 特性开关
    */
   Map<String, Boolean> feature;
   /**
    * 剪贴板.
    */
   Map<String, String> clipboard;
}
