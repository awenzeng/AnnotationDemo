package com.awen.annotationdemo;

/**
 * Created by Administrator on 2017/8/1.
 */

public interface UserInterface {
    @UserMethod(title = "AwenZeng")
    String getUser(@UserParam(name = "刘峰",phone = "110") String a);
}
