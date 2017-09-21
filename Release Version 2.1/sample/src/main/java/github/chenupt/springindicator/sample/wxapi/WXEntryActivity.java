package github.chenupt.springindicator.sample.wxapi;

/**
 * Created by chenlingfei123 on 2015/12/4.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import github.chenupt.springindicator.sample.MainActivity;

/** 微信客户端回调activity示例 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, MainActivity.APP_ID,false);
        api.handleIntent(getIntent(), this);

        super.onCreate(savedInstanceState);
    }
    @Override
    public void onReq(BaseReq arg0) { }

    @Override
    public void onResp(BaseResp resp) {
        Toast toast;

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
                toast = Toast.makeText(this,
                        "分享成功",
                        Toast.LENGTH_SHORT);
                toast.show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
                toast = Toast.makeText(this,
                        "分享取消",
                        Toast.LENGTH_SHORT);
                toast.show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                toast = Toast.makeText(this,
                        "分享拒绝",
                        Toast.LENGTH_SHORT);
                toast.show();
                break;
            default:
                toast = Toast.makeText(this,
                        "分享失败",
                        Toast.LENGTH_SHORT);
                toast.show();
        }
        finish();

    }
}
