import { defineStore } from "pinia";
import axios from "axios";
import { useAuthStore } from "./auth";

export const useUserStore = defineStore({
  id: "user",
  state: () => ({
    email: "",
    firstName: "",
    lastName: "",
    phoneNumber: "",
    profilePictureUrl: null,
    birthDate: "",
    users: [],
  }),
  actions: {
    async getUserDetails() {
      try {
        const auth = useAuthStore();
				const token = auth.token;

          const response = await axios.get('http://localhost:8080/api/v1/profile', {
              headers: {
                  Authorization: `Bearer ${token}`,
              },
              params: {
                  email: auth.user,
              },
          });

          const userDetails = response.data;

          this.email = userDetails.email;
          this.firstName = userDetails.firstName;
          this.lastName = userDetails.lastName;
          this.phoneNumber = userDetails.phoneNumber;
          this.profilePictureUrl = userDetails.profilePictureUrl;
          this.birthDate = userDetails.birthDate;
          this.role = userDetails.role;

          console.log('User details:', userDetails);
      } catch (error) {
          console.error('Error fetching user details:', error);
      }
  },
    async getAllUsers() {
      try {
        const response = await axios.get("http://localhost:8080/api/v1/users", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });

        this.users = response.data;

        console.log("User details:", this.users);
      } catch (error) {
        console.error("Error fetching user details:", error);
      }
    },
    async editUser(user) {
      try {
        const response = await axios.put(
          `http://localhost:8080/api/v1/admin/edit-user/${user.email}`,
          user,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );

        console.log("User edited successfully:", response.data);
      } catch (error) {
        console.error("Error editing user:", error);
        throw error;
      }
    },
    async deleteUser(user) {
      try {
        const response = await axios.delete(
          `http://localhost:8080/api/v1/admin/delete-user/${user.email}`,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );

        console.log("User deleted successfully:", response.data);

        this.users = this.users.filter((user) => user.email !== user.email);
      } catch (error) {
        console.error("Error deleting user:", error);
        throw error;
      }
    },
    async updateFirstName(newFirstName) {
      try {
        const response = await axios.put(
          "http://localhost:8080/api/v1/profile/update-first-name",
          {
            firstName: newFirstName,
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        this.firstName = newFirstName;
        console.log(response.data, newFirstName);
      } catch(error) {
        console.error("Error updating first name: ", error);
        throw error;
      }
    },
    async updateLastName(newLastName) {
      try {
        const response = await axios.put(
          "http://localhost:8080/api/v1/profile/update-last-name",
          {
            lastName: newLastName,
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        this.lastName = newLastName;
        console.log(response.data), newLastName;
      } catch (error) {
        console.error("Error updating last name: ", error);
        throw(error);
      }
    },
    async updatePhoneNumber(newPhoneNumber) {
      try {
        const response = await axios.put(
          "http://localhost:8080/api/v1/profile/update-phone-number",
          {
            phoneNumber: newPhoneNumber,
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        this.phoneNumber = newPhoneNumber;
        console.log(response.data, newPhoneNumber);
      } catch (error) {
        console.error("Error updating phone number: ", error);
        throw error;
      }
    },

    async updateBirthDate(newBirthDate) {
      try {
        await axios.put(
          "http://localhost:8080/api/v1/profile/update-birthdate",
          {
            birthDate: newBirthDate, 
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        this.birthDate = newBirthDate;
        console.log("Birthdate updated successfully:", newBirthDate);
      } catch(error) {
        console.error("Error updating birthdate:", error);
        throw error;
      }
    },
    
    async deactivateUser(user) {
      try {
        const response = await axios.patch(
          `http://localhost:8080/api/v1/admin/deactivate-user/${user.email}`,
          null,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );

        console.log("User deactivated successfully:", response.data);

        await this.getAllUsers();
      } catch (error) {
        console.error("Error deactivating user:", error);
        throw error;
      }
    },
    async activateUser(user) {
      try {
        const response = await axios.patch(
          `http://localhost:8080/api/v1/admin/activate-user/${user.email}`,
          null,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );

        console.log("User activated successfully:", response.data);

        await this.getAllUsers();
      } catch (error) {
        console.error("Error activating user:", error);
        throw error;
      }
    },
    searchUsers(query) {
      if (!query) {
        return this.users;
      } else {
        return this.users.filter(
          (user) =>
            user.firstName.toLowerCase().includes(query.toLowerCase()) ||
            user.lastName.toLowerCase().includes(query.toLowerCase())
        );
      }
    },
    async changePassword(email, currentPassword, newPassword) {
      try {
        const response = await axios.patch(
          `http://localhost:8080/api/v1/admin/change-password/${email}`,
          {
            email,
            currentPassword,
            newPassword,
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
    
        console.log("Password changed successfully:", response.data);
        return response.data;
      } catch (error) {
        console.error("Error changing password:", error);
        throw error;
      }
    },

    async fetchSasToken() {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/generate-sas-token');
        this.sasToken = response.data;
      } catch (error) {
        console.error('Error fetching SAS token:', error.response || error.message);
        throw error;
      }
    },
    async uploadProfilePictureWithSasToken(file) {
      try {
        const auth = useAuthStore();
        const token = auth.token;

        await this.fetchSasToken();

        const formData = new FormData();
        formData.append('profilePicture', file);

        const response = await axios.post('http://localhost:8080/api/v1/profile/upload-profile-picture', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            Authorization: `Bearer ${token}`,
          },
        });

        console.log('Profile picture uploaded successfully:', response.data);

        await this.getUserDetails();
      } catch (error) {
        console.error('Error uploading profile picture:', error);
        throw error;
      }
    },
    async deleteProfilePicture() {
      try {
        const auth = useAuthStore();
        const token = auth.token;

        const response = await axios.delete(
          'http://localhost:8080/api/v1/profile/delete-profile-picture',
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        console.log('Profile picture deleted successfully:', response.data);

        this.profilePictureUrl = null;
        console.log('Profile picture deleted successfully:', response.data);
      } catch (error) {
        console.error('Error deleting profile picture:', error);
        throw error;
      }
    },

    logout() {
      this.email = "";
      this.firstName = "";
      this.lastName = "";
      this.phoneNumber = "";
      this.profilePictureUrl = null;
      this.birthDate = "";
      this.users = [];
    },
  },
});

export const fetchUserProfile = async (email) => {
    try {
        const response = await axios.get(`http://localhost:8080/api/v1/user/${email}`, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
        });

        console.log('User details response:', response.data);

        return response.data;
    } catch (error) {
        console.error('Error fetching user details:', error);
        throw error;
    }
};
