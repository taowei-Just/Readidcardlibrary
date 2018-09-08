package com.it_tao_idcard.bluetooth;

/**
 * Created by Tao on 2018/7/27 0027.
 */

public interface BleSocketCall {

    void  onException();
    void  onConnect();
    void  onDisConnect();

}
