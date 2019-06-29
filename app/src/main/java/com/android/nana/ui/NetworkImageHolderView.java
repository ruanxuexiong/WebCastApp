package com.android.nana.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.BannerEntity;
import com.bigkoo.convenientbanner.holder.Holder;
import com.squareup.picasso.Picasso;

public class NetworkImageHolderView implements Holder<BannerEntity> {

    private ImageView mIvPicture;
    private TextView mTxtName, mTxtDesc;

    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_index_top, null);
        mIvPicture = (ImageView) view.findViewById(R.id.home_index_iv_webcast_picture);
      //  mTxtName = (TextView) view.findViewById(R.id.home_index_txt_webcast_name);
      //  mTxtDesc = (TextView) view.findViewById(R.id.home_index_txt_webcast_desc);
       // UIUtil.getInstance().setLayoutParams(mIvPicture, (int) (0.48 * StringHelper.getScreenWidth((Activity) context)));
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, BannerEntity data) {
       /* if (!"".equals(data.getName())) {
            mTxtName.setText(data.getName());
        }*/

      /*  if (!"".equals(data.getIntroduce())) {
            mTxtDesc.setText(data.getIntroduce());
        }
*/
        Picasso.with(context).load(data.getPicture()).error(com.android.common.R.mipmap.icon_defult)
                .placeholder(com.android.common.R.mipmap.icon_defult).fit().into(mIvPicture);
    }
}