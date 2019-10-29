package ta.na.mao.communications.http.auth.get;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class getUser implements Serializable {

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @SerializedName("user")
    private User user;

    public static class User implements Serializable {

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @SerializedName("last_name")
        private String lastName;
        @SerializedName("id")
        private long id;
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("credits")
        private int credits;
        @SerializedName("last_activity")
        private String last_activity;


        public int getCredits() {
            return credits;
        }

        public void setCredits(int credits) {
            this.credits = credits;
        }

        public String getLast_activity() {
            return last_activity;
        }

        public void setLast_activity(String last_activity) {
            this.last_activity = last_activity;
        }
    }

}