package com.android.nana.bean;

public class UserInfo {

	String id;
	String user_login;
	String user_nicename;
	String username;
	String user_email;
	String user_url;
	String avatar;
	String sex;
	String birthday;
	String signature;
	String last_login_ip;
	String last_login_time;
	String create_time;
	String user_activation_key;
	String user_status;
	String score;
	String user_type;
	String coin;
	String mobile;
	String title;
	String backgroundImage;
	String age;
	String introduce;
	String purposeId;
	String provinceId;
	String cityId;
	String districtId;
	String province;
	String city;
	String district;
	String money;
	String balance;
	String payPassword;
	String login_type;
	String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLogin_type() {
		return login_type;
	}

	public void setLogin_type(String login_type) {
		this.login_type = login_type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getDistrictId() {
		return districtId;
	}
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	public String getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(String purposeId) {
		this.purposeId = purposeId;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getBackgroundImage() {
		return backgroundImage;
	}
	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser_login() {
		return user_login;
	}
	public void setUser_login(String user_login) {
		this.user_login = user_login;
	}
	public String getUser_nicename() {
		return user_nicename;
	}
	public void setUser_nicename(String user_nicename) {
		this.user_nicename = user_nicename;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public String getUser_url() {
		return user_url;
	}
	public void setUser_url(String user_url) {
		this.user_url = user_url;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getLast_login_ip() {
		return last_login_ip;
	}
	public void setLast_login_ip(String last_login_ip) {
		this.last_login_ip = last_login_ip;
	}
	public String getLast_login_time() {
		return last_login_time;
	}
	public void setLast_login_time(String last_login_time) {
		this.last_login_time = last_login_time;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getUser_activation_key() {
		return user_activation_key;
	}
	public void setUser_activation_key(String user_activation_key) {
		this.user_activation_key = user_activation_key;
	}
	public String getUser_status() {
		return user_status;
	}
	public void setUser_status(String user_status) {
		this.user_status = user_status;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
	public String getCoin() {
		return coin;
	}
	public void setCoin(String coin) {
		this.coin = coin;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public UserInfo(String id, String user_login, String username, String user_nicename,
			String user_email, String user_url, String avatar, String sex,
			String birthday, String signature, String last_login_ip,
			String last_login_time, String create_time,
			String user_activation_key, String user_status, String score,
			String user_type, String coin, String mobile,String title,
			String backgroundImage,String age,String introduce,String purposeId,
			String provinceId, String cityId, String districtId,
			String province,String city,String district,String money,String balance,
					String payPassword,String login_type,String status) {
		super();
		this.id = id;
		this.user_login = user_login;
		this.username = username;
		this.user_nicename = user_nicename;
		this.user_email = user_email;
		this.user_url = user_url;
		this.avatar = avatar;
		this.sex = sex;
		this.birthday = birthday;
		this.signature = signature;
		this.last_login_ip = last_login_ip;
		this.last_login_time = last_login_time;
		this.create_time = create_time;
		this.user_activation_key = user_activation_key;
		this.user_status = user_status;
		this.score = score;
		this.user_type = user_type;
		this.coin = coin;
		this.mobile = mobile;
		this.title = title;
		this.backgroundImage = backgroundImage;
		this.age = age;
		this.introduce = introduce;
		this.purposeId = purposeId;
		this.provinceId = provinceId;
		this.cityId = cityId;
		this.districtId = districtId;
		this.province = province;
		this.city = city;
		this.district = district;
		this.money = money;
		this.balance = balance;
		this.payPassword = payPassword;
		this.login_type = login_type;
		this.status = status;
	}

	public UserInfo(String id,String username,String avatar){
		this.id = id;
		this.username = username;
		this.avatar = avatar;
	}

}
