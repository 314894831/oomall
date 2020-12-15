package cn.edu.xmu.other.service;

import cn.edu.xmu.ooad.util.ReturnObject;

public interface CustomerServiceInterface {
    public ReturnObject login(String userName, String password, String ipAddr);
}
