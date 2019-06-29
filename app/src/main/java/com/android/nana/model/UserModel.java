package com.android.nana.model;

import android.content.Context;
import android.content.Intent;

import com.android.common.BaseApplication;
import com.android.common.models.BaseModel;
import com.android.common.models.DBModel;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.common.utils.StringHelper;
import com.android.nana.material.EditDataActivity;
import com.android.nana.webview.WebViewActivity;
import com.android.nana.bean.BannerEntity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.home.CustomerDetailActivity;
import com.android.nana.ui.NetworkImageHolderView;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.CategoryListActivity;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.lidroid.xutils.http.RequestParams;

import java.util.List;

/**
 * Created by Administrator on 2017/5/20 0020.
 */

public class UserModel {

    public static void doBanner(final Context context, ConvenientBanner convenientBanner, final List<BannerEntity> list) {

        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, list).startTurning(4000).setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                doPicture(context, list.get(position));
            }
        });

        if (list != null && list.size() == 1) {
            convenientBanner.setCanLoop(false);
        }

    }

    public static void doPicture(Context context, BannerEntity banner) {

        String url = banner.getUrl();
        switch (banner.getType()) {
            case "1": // 打开主播详情（url为主播id）
                if (!StringHelper.checkInt(url)) return;
                if (BaseApplication.getInstance().getCustomerId(context).equals(url)) {
                    context.startActivity(new Intent(context, CustomerDetailActivity.class));
                } else {
                    Intent intent = new Intent(context, EditDataActivity.class);
                    intent.putExtra("UserId", url);
                    context.startActivity(intent);
                }
                break;
            case "2": // 打开某个分类（url为“大类id，小类id）
                if (!url.contains(",")) return;
                String categoryId = url.split(",")[0];
                String childCategoryId = url.split(",")[1];
                Intent intent = new Intent(context, CategoryListActivity.class);
                intent.putExtra("Name", "分类");
                intent.putExtra("CategoryId", categoryId);
                intent.putExtra("ChildCategoryId", childCategoryId);
                break;
            case "3": // 打开url

                if (!url.contains("http://")) return;

                String thisUserId = (String) SharedPreferencesUtils.getParameter(context, "userId", "");
                if (null != thisUserId) {
                    Intent intentWeb = new Intent(context, WebViewActivity.class);
                    intentWeb.putExtra("Url", url);
                    intentWeb.putExtra("share_title", banner.getShare_title());
                    intentWeb.putExtra("share_content", banner.getShare_content());
                    intentWeb.putExtra("share_pic", banner.getShare_pic());
                    intentWeb.putExtra("share_url", banner.getShare_url());
                    intentWeb.putExtra("thisUserId", thisUserId);
                    context.startActivity(intentWeb);
                } else {
                    ToastUtils.showToast("请先登录");
                }

                break;
        }

    }

    public static void queryUserSeeTimeLong(final OnUserSeeTimeLongListener onUserSeeTimeLongListener) {

        CustomerDbHelper.queryUserSeeTimeLong(new IOAuthCallBack() {

            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                BaseModel mResultDetailModel = new ResultRequestModel(successJson);
                if (mResultDetailModel.mIsSuccess) {
                    String userSeeTimeLong = JSONUtil.get(mResultDetailModel.mJsonData, "seeTimeLong", "");
                    DBModel dbModel = DBModel.get("userSeeTimeLong");
                    if (dbModel != null) {
                        if (!dbModel.Description.equals(userSeeTimeLong)) {
                            DBModel.saveOrUpdate("userSeeTimeLong", userSeeTimeLong);
                        }
                    } else {
                        DBModel.saveOrUpdate("userSeeTimeLong", userSeeTimeLong);
                    }
                    if (onUserSeeTimeLongListener != null) {
                        onUserSeeTimeLongListener.onSuccess();
                    }
                }

            }

            @Override
            public void getFailue(String failueJson) {

            }
        });

    }

    public interface OnUserSeeTimeLongListener {
        void onSuccess();
    }


    /**
     * 获取通话时长
     */
    public static void getTiemLong(String id, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("id", id);
        HttpRequest.post("Order/getTiemLong", requestParams, callBack);
    }

}
