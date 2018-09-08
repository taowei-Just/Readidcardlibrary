package com.it_tao_idcard.bluetooth;

import com.it_tao_idcard.ReadIDCardInform.info.IDcardInfo;

/**
 * Created by Tao on 2018/7/30 0030.
 */

public interface ReadInfoCall {
    void  onReadSuccess(IDcardInfo info);
}
