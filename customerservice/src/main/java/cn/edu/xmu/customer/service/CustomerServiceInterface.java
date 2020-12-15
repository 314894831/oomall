package cn.edu.xmu.customer.service;

import cn.edu.xmu.ooad.util.ReturnObject;

public interface CustomerServiceInterface {
    public ReturnObject login(String userName, String password, String ipAddr);
}
