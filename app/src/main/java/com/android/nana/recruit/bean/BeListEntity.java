package com.android.nana.recruit.bean;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/23.
 */

public class BeListEntity {
    private ArrayList<User> user;
    private String pass;
    private Company company;

    public ArrayList<User> getUser() {
        return user;
    }

    public void setUser(ArrayList<User> user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }




    public class User {
        private String positionId;
        private String position;
        private ArrayList<UserList> userList;

        public String getPositionId() {
            return positionId;
        }

        public void setPositionId(String positionId) {
            this.positionId = positionId;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public ArrayList<UserList> getUserList() {
            return userList;
        }

        public void setUserList(ArrayList<UserList> userList) {
            this.userList = userList;
        }


        public class UserList {

            private UserInfo userInfo;
            private Job job;
            private String range;

            public UserInfo getUserInfo() {
                return userInfo;
            }

            public void setUserInfo(UserInfo userInfo) {
                this.userInfo = userInfo;
            }

            public Job getJob() {
                return job;
            }

            public void setJob(Job job) {
                this.job = job;
            }

            public String getRange() {
                return range;
            }

            public void setRange(String range) {
                this.range = range;
            }


            public class UserInfo {
                private String id;
                private String username;
                private String user_nicename;
                private String birthday;
                private String introduce;
                private String sex;
                private String avatar;
                private String provinceId;
                private String cityId;
                private String backgroundImage;
                private String status;
                private String uname;
                private String workage;
                private String qualification;
                private String province;
                private String city;
                private String position;
                private String company;
                private String advantage;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getUsername() {
                    return username;
                }

                public void setUsername(String username) {
                    this.username = username;
                }

                public String getUser_nicename() {
                    return user_nicename;
                }

                public void setUser_nicename(String user_nicename) {
                    this.user_nicename = user_nicename;
                }

                public String getBirthday() {
                    return birthday;
                }

                public void setBirthday(String birthday) {
                    this.birthday = birthday;
                }

                public String getIntroduce() {
                    return introduce;
                }

                public void setIntroduce(String introduce) {
                    this.introduce = introduce;
                }

                public String getSex() {
                    return sex;
                }

                public void setSex(String sex) {
                    this.sex = sex;
                }

                public String getAvatar() {
                    return avatar;
                }

                public void setAvatar(String avatar) {
                    this.avatar = avatar;
                }

                public String getProvinceId() {
                    return provinceId;
                }

                public void setProvinceId(String provinceId) {
                    this.provinceId = provinceId;
                }

                public String getCityId() {
                    return cityId;
                }

                public void setCityId(String cityId) {
                    this.cityId = cityId;
                }

                public String getBackgroundImage() {
                    return backgroundImage;
                }

                public void setBackgroundImage(String backgroundImage) {
                    this.backgroundImage = backgroundImage;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getUname() {
                    return uname;
                }

                public void setUname(String uname) {
                    this.uname = uname;
                }

                public String getWorkage() {
                    return workage;
                }

                public void setWorkage(String workage) {
                    this.workage = workage;
                }

                public String getQualification() {
                    return qualification;
                }

                public void setQualification(String qualification) {
                    this.qualification = qualification;
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

                public String getPosition() {
                    return position;
                }

                public void setPosition(String position) {
                    this.position = position;
                }

                public String getCompany() {
                    return company;
                }

                public void setCompany(String company) {
                    this.company = company;
                }

                public String getAdvantage() {
                    return advantage;
                }

                public void setAdvantage(String advantage) {
                    this.advantage = advantage;
                }


            }

            public class Job {
                private String status;
                private String currentCompany;
                private String currentJob;
                private String position;
                private String province;
                private String city;
                private String area;

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getCurrentCompany() {
                    return currentCompany;
                }

                public void setCurrentCompany(String currentCompany) {
                    this.currentCompany = currentCompany;
                }

                public String getCurrentJob() {
                    return currentJob;
                }

                public void setCurrentJob(String currentJob) {
                    this.currentJob = currentJob;
                }

                public String getPosition() {
                    return position;
                }

                public void setPosition(String position) {
                    this.position = position;
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

                public String getArea() {
                    return area;
                }

                public void setArea(String area) {
                    this.area = area;
                }

                public String getProfessions() {
                    return professions;
                }

                public void setProfessions(String professions) {
                    this.professions = professions;
                }

                private String professions;
            }


        }
    }

    public class Company {
        private String id;
        private String company;
        private String professonId;
        private String scaleId;
        private String financId;
        private String website;
        private String highlights;
        private String introduction;
        private String mid;
        private String certification;
        private String logo;
        private String pass;
        private String legal;
        private String capital;
        private String type;
        private String code;
        private String establish;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getProfessonId() {
            return professonId;
        }

        public void setProfessonId(String professonId) {
            this.professonId = professonId;
        }

        public String getScaleId() {
            return scaleId;
        }

        public void setScaleId(String scaleId) {
            this.scaleId = scaleId;
        }

        public String getFinancId() {
            return financId;
        }

        public void setFinancId(String financId) {
            this.financId = financId;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getHighlights() {
            return highlights;
        }

        public void setHighlights(String highlights) {
            this.highlights = highlights;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getCertification() {
            return certification;
        }

        public void setCertification(String certification) {
            this.certification = certification;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public String getLegal() {
            return legal;
        }

        public void setLegal(String legal) {
            this.legal = legal;
        }

        public String getCapital() {
            return capital;
        }

        public void setCapital(String capital) {
            this.capital = capital;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getEstablish() {
            return establish;
        }

        public void setEstablish(String establish) {
            this.establish = establish;
        }


    }
}
