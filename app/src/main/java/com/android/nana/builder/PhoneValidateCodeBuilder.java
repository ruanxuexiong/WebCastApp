package com.android.nana.builder;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.builder.CountDownBuilder;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.StringHelper;
import com.android.nana.dbhelper.LoginDbHelper;

public class PhoneValidateCodeBuilder implements CountDownBuilder.OnCountRunning {

	private Context mContext;
	private EditText mEditPhone;
	private TextView mTxtValidateCode;
	private boolean mIsAllowed;

	private String mPhoneNumber;
	private CountDownBuilder mCountDownBuilder;

	public PhoneValidateCodeBuilder(Context context, EditText editPhone, TextView validateCode) {
		mContext = context;
		mEditPhone = editPhone;
		mTxtValidateCode = validateCode;
		mCountDownBuilder = new CountDownBuilder(this);

	}

	public PhoneValidateCodeBuilder(Context context, String phone, TextView validateCode) {
		mContext = context;
		mPhoneNumber = phone;
		mTxtValidateCode = validateCode;
		mCountDownBuilder = new CountDownBuilder(this);

	}

	public void getCardByPhone(String messageType, final PhoneValidateCodeListener validateCodeListener){
		
		if (!mIsAllowed) return;
		
		String phone = getPhoneNumber();
		
		LoginDbHelper.sendMessage(phone, messageType, new IOAuthCallBack() {
			@Override
			public void onStartRequest() {
			}
			
			@Override
			public void getSuccess(String successJson) {
				if (validateCodeListener != null) {
					validateCodeListener.success(successJson);
				}
			}
			
			@Override
			public void getFailue(String failueJson) {
				
				if (validateCodeListener != null) {
					validateCodeListener.failue(failueJson);
				}
			}
		});
		
	}
	
	public void countDown(int count) {
		
		if (!mIsAllowed) return;
		
		mCountDownBuilder.Run(count);
		
	}
	
	public void verificationFormat(String phoneNullStr, String phoneErrorStr){
		String phone = getPhoneNumber();
		if (TextUtils.isEmpty(phone)) {
			showToast(phoneNullStr);
			return;
		}
		if (!StringHelper.checkMobileNumber(phone)) {
			showToast(phoneErrorStr);
			return;
		}
		mIsAllowed = true;
		mTxtValidateCode.setEnabled(false);
	}

	private String getPhoneNumber(){

		if (mEditPhone != null) {

			return mEditPhone.getText().toString().trim();
		} else {

			return mPhoneNumber;
		}

	}

	private void showToast(String msg) {
		
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRuning(int second) {
		mTxtValidateCode.setText(second + "秒");
	}
	
	@Override
	public void onFinished() {
		mTxtValidateCode.setEnabled(true);
		mTxtValidateCode.setText("获取验证码");
	}
	
	public interface PhoneValidateCodeListener{
		
		void success(String result);
		void failue(String failue);
	}
	
}
