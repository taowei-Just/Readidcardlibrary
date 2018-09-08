package com.sdt;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Tao on 2018/7/21 0021.
 */
public  abstract  class Api {

  public Sdt usbapi;

    Common common = new Common();

  abstract   public  int SDT_ResetSAM()throws IOException;

    abstract public int SDT_GetSAMStatus()throws IOException;

    abstract public int SDT_GetSAMID(byte[] pucSAMID)throws IOException ;

    abstract public int SDT_GetSAMIDToStr(char[] pucSAMID) throws IOException ;

    abstract public int SDT_StartFindIDCard() throws IOException;

    abstract  public int SDT_SelectIDCard() throws IOException;

    /**
     *   读取身份信息
     * @param pucCHMsg
     * @param puiCHMsgLen
     * @param pucPHMsg
     * @param puiPHMsgLen
     * @return
     */
    abstract   public int SDT_ReadBaseMsg(byte[] pucCHMsg, int[] puiCHMsgLen, byte[] pucPHMsg, int[] puiPHMsgLen) throws IOException;

    abstract  public int SDT_ReadBaseFPMsg(byte[] pucCHMsg, int[] puiCHMsgLen, byte[] pucPHMsg, int[] puiPHMsgLen, byte[] pucFPMsg, int[] puiFPMsgLen) throws IOException;

    abstract void SamIDIntTostr(byte[] pucSAMID, char[] pcSAMID) throws IOException;

    abstract  public int byte2int(byte b) ;

    abstract  public void initUsb(Context context) throws Exception ;

   public abstract void close() throws Exception;
}
