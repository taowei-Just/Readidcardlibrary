package com.it_tao_idcard.ReadIDCardInform.callback;

import com.it_tao_idcard.ReadIDCardInform.info.IDcardInfo;

/**
 * Created by Administrator on 2017-09-07.
 */

public interface IdcardReadCallback {
    
    
    public void  onReadSucces(IDcardInfo information);
    
    public void  onReadSataeexception();
    
    public void  onReadFailed();
    
    public void  onReadTimeout();
    
    
    
}
