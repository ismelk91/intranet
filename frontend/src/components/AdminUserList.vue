<template>
  <div class="container">
    <SearchBar @search="updateSearchQuery" placeholder="search employee" class="search-input" />

    <table class="user-list">
      <thead>
        <tr>
          <th class="first">Name</th>
          <th>Email</th>
          <th>Phone nr</th>
          <th>Role</th>
          <th class="last"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in filteredUsers" :key="user.email" class="user-list">
          <td>
            <img v-if="user.profilePictureUrl" :src="user.profilePictureUrl"
              class="avatar sm rounded-pill me-3 flex-shrink-0"
              :alt="user.firstName + ' ' + user.lastName + ' Profile Picture'">
            <img v-else src="../assets/no-profile-picture.png" class="avatar sm rounded-pill me-3 flex-shrink-0"
              alt="No Profile Picture">
            <router-link :to="`/user/${user.email}`" class="user-item">
              {{ user.firstName }} {{ user.lastName }}
            </router-link>
          </td>
          <td>{{ user.email }}</td>
          <td>{{ user.phoneNumber }}</td>
          <td>{{ user.role }}</td>
          <td>
            <div class="dropdown" @click="toggleDropdown(user)">
              <i class="bi bi-three-dots-vertical"></i>
              <div v-if="selectedUser === user" class="dropdown-content">
                <a href="#" @click="togglePopup(user)">Edit</a>
                <a href="#" @click="deleteUser(user)">Delete</a>
              </div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>


  <div class="viewmore-container">
    <button class="viewmore-btn btn btn-primary" @click="loadMoreUsers" v-if="filteredUsers.length >= 10">
      Load More
    </button>
  </div>

  <div v-if="isPopupVisible && selectedUser" class="admin-popup">
    <div class="admin-popup-content">
      <i class="admin-toggle bi bi-x" @click="togglePopup(user)"></i>

      <div class="row mb-3">
        <div class="label">
          <h6 class="mb-0">First name</h6>
        </div>
        <div class="value text-secondary">
          <input type="text" class="form-control" v-model="selectedUser.firstName" placeholder="first name">
        </div>
      </div>

      <div class="row mb-3">
        <div class="label">
          <h6 class="mb-0">Last name</h6>
        </div>
        <div class="value text-secondary">
          <input type="text" class="form-control" v-model="selectedUser.lastName">
        </div>
      </div>

      <div class="row mb-3">
        <div class="label">
          <h6 class="mb-0">Birthdate</h6>
        </div>
        <div class="value text-secondary">
          <input type="date" class="form-control" v-model="selectedUser.birthDate">
        </div>
      </div>

      <div class="row mb-3">
        <div class="label">
          <h6 class="mb-0">Email</h6>
        </div>
        <div class="value text-secondary">
          <input type="text" class="form-control" :value="selectedUser.email" disabled>
        </div>
      </div>

      <div class="row mb-3">
        <div class="label">
          <h6 class="mb-0">Phone number</h6>
        </div>
        <div class="value text-secondary">
          <input type="text" class="form-control" v-model="selectedUser.phoneNumber">
        </div>
      </div>

      <div class="row mb-3">
        <div class="label">
          <h6 class="mb-0">Change password</h6>
        </div>
        <div class="value text-secondary d-flex align-items-center">
          <input v-model="selectedUser.currentPassword" class="password-input form-control me-2" type="password"
            placeholder="current password" />
          <input v-model="selectedUser.newPassword" class="password-input form-control me-2" type="password"
            placeholder="new password" />
        </div>
      </div>

      <button class="btn btn-primary btn-activate" @click="activateUser(selectedUser)">
        Activate
      </button>
      <button class="btn btn-deactivate" @click="deactivateUser(selectedUser)">
        Inactivate
      </button>

    </div>


    <div class="bottom-right">
      <button class="btn-save btn btn-primary" @click="saveChanges(selectedUser)">
        Save
      </button>
      <button class="btn-cancel btn btn-primary" @click="togglePopup(user)">
        Cancel
      </button>
    </div>

  </div>

  <Overlay v-if="isPopupVisible" close-overlay="togglePopup" />
</template>

<script>
import { onMounted, ref, computed } from 'vue';
import { useUserStore } from '@/stores/user';
import SearchBar from './SearchBar.vue';
import Overlay from './OverlayComponent.vue';

export default {
  components: {
    SearchBar,
    Overlay
  },

  setup() {
    const userStore = useUserStore();
    const searchQuery = ref("");
    const newPassword = ref("");
    const currentPassword = ref("");
    const displayedUsersCount = ref(10);
    const isPopupVisible = ref(false);
    const selectedUser = ref(null);

    const getUsers = async () => {
      try {
        await userStore.getAllUsers();
      } catch (error) {
        console.error("Error fetching user profiles:", error);
      }
    };

    const toggleDropdown = (user) => {
      selectedUser.value = user === selectedUser.value ? null : user;
    }

    const togglePopup = (user) => {
      selectedUser.value = user === selectedUser.value ? null : user;
      isPopupVisible.value = !isPopupVisible.value;
    };

    const saveChanges = async () => {
      if (selectedUser.value) {
        try {
          await userStore.editUser(selectedUser.value);
          getUsers();
          console.log("User updated:", selectedUser.value);
          isPopupVisible.value = false;
        } catch (error) {
          console.error("Error updating user:", error);
        }
      }
    };


    const editUser = (user) => {
      userStore.setEmail(user.email);
      userStore.setFirstName(user.firstName);
      userStore.setLastName(user.lastName);
      userStore.setPhoneNumber(user.phoneNumber);
      userStore.setRole(user.role);
    };

    const deleteUser = async (user) => {
      try {
        await userStore.deleteUser(user);
        getUsers();
      } catch (error) {
        console.error("Error deleting user:", error);
      }
    };

    const deactivateUser = async (user) => {
      try {
        await userStore.deactivateUser(user);
        getUsers();
      } catch (error) {
        console.error("Error deactivating user:", error);
      }
    };

    const activateUser = async (user) => {
      try {
        await userStore.activateUser(user);
        getUsers();
      } catch (error) {
        console.error("Error activating user:", error);
      }
    };

    const filteredUsers = computed(() => {
      const allUsers = userStore.searchUsers(searchQuery.value);
      return allUsers.slice(0, displayedUsersCount.value);
    });

    const updateSearchQuery = (value) => {
      searchQuery.value = value;
    };

    const setUserPassword = (user, password) => {
      user.currentPassword = password.current;
      user.newPassword = password.new;
    };

    const changePassword = async (user) => {
      try {
        await userStore.changePassword(
          user,
          user.currentPassword,
          user.newPassword
        );

        user.newPassword = "";
        user.currentPassword = "";
      } catch (error) {
        console.error("Error changing password:", error);
      }
    };

    const loadMoreUsers = () => {
      displayedUsersCount.value += 10;
    };

    onMounted(() => {
      getUsers();
    });

    return {
      user: userStore,
      saveChanges,
      editUser,
      deleteUser,
      deactivateUser,
      activateUser,
      searchQuery,
      filteredUsers,
      loadMoreUsers,
      newPassword,
      currentPassword,
      changePassword,
      setUserPassword,
      isPopupVisible,
      togglePopup,
      updateSearchQuery,
      selectedUser,
      toggleDropdown
    };
  },
};
</script>

<style scoped>
.search-input {
  width: 980px;
  padding: 13px 0 13px 55px;
  background-color: var(--blue);
  color: var(--white);
  border: none;
  border-radius: 50px;
  outline: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='white' class='bi bi-search' viewBox='0 0 16 16'%3E%3Cpath d='M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: 20px center;
  background-size: 20px;
  font-size: 16px;
}

.search-input::placeholder {
  color: var(--white);
}

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 50px;
}

.user-list {
  margin-top: 30px;
  background-color: var(--blue);
  width: 980px;
  border-radius: 9px;
  color: var(--white);
  position: relative;
}

.user-list th {
  padding: 10px;
  font-size: 14px;
  background-color: #222E58;
}

.user-list th.first {
  border-top-left-radius: 9px;
}

.user-list th.last {
  border-top-right-radius: 9px;
}

.user-list td {
  padding: 10px;
  font-size: 16px;
}

.user-item {
  color: var(--white);
  text-decoration: none;
}

.user-item:hover {
  text-decoration: underline;
}

.avatar.sm {
  width: 35px;
  height: 35px;
}

.dropdown {
  cursor: pointer;
  font-size: 18px;
}

.dropdown-content {
  position: absolute;
  background-color: #282828;
  min-width: 80px;
  box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
  z-index: 1;
  top: 0;
  left: 20px;
  border-radius: 3px;
}

.dropdown-content a {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
  color: var(--white);
  padding: 5px 10px;
  text-decoration: none;
  display: block;
}

.dropdown-content a:hover {
  text-decoration: underline;
}


.btn {
  background-color: #223266;
  color: var(--white);
  padding: 6px 10px;
  border-radius: 5px;
  border: none;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  transition: background-color 0.3s ease, color 0.3s ease;
  margin-right: 5px;
}

.btn:hover {
  background-color: var(--orange);
}

.btn-change-password,
.btn-deactivate,
.btn-activate {
  margin-top: 10px;
}

.btn-delete:hover,
.btn-deactivate:hover {
  color: #fff;
  background-color: #df3232;
}

.button-container {
  display: flex;
}

.admin-toggle {
  position: absolute;
  top: 0;
  right: 0;
  height: 35px;
  width: 40px;
  font-size: 25px;
  cursor: pointer;
  transition: 0.3s;
  text-align: center;
  border-top-right-radius: 3px;
}

.admin-toggle:hover {
  background-color: #df3232;
  color: #fff;
  cursor: pointer;
}

.admin-popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  background-color: var(--blue);
  color: var(--white);
  border-radius: 3px;
  z-index: 9;
}

.admin-popup-content {
  margin-top: 10px;
}

.label {
  margin-bottom: 5px;
}

.admin-popup input:focus {
  outline: none;
}

.form-control {
  width: 100%;
  background-color: var(--white);
  color: black;
}

.form-control[disabled] {
  background-color: #ccc;
  color: #555;
}

.password-input {
  width: 50%;
  background-color: var(--white);
  color: black;
}

.bottom-right {
  position: absolute;
  bottom: 20px;
  right: 10px;
}

.viewmore-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.viewmore-btn {
  padding: 5px 15px;
  border-radius: 9px;
  background-color: var(--blue);
  border-color: var(--blue);
  color: var(--white);
}

.viewmore-btn:hover {
  border: 1px solid var(--orange);
}
</style>