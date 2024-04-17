<template>
    <div class="dropdown" @click="profileToggle" :class="{ active: showContent }">
      <div class="user-info">
          <i class="bi bi-chevron-down"></i>
        <span v-if="user.firstName" class="user-name">{{ user.firstName }}</span>
        <img v-if="user.profilePictureUrl" :src="user.profilePictureUrl" :alt="user.firstName + user.lastName + ' Profile Picture'" />
        <img v-else src="../assets/no-profile-picture.png" alt="No Profile Picture" width="40" height="40" />
      </div>
      <div class="dropdown-content" :style="{ display: showContent ? 'block' : 'none' }">
        <router-link to="/profile"><i class="bi bi-person-circle"></i> Profile</router-link> 
        <a href="#" @click="logout"><i class="bi bi-box-arrow-right"></i> Logout</a>
      </div>
    </div>
  </template>
  
  
  <script>
  import { onMounted, ref } from 'vue';
  import { useUserStore } from '@/stores/user';
    import { useAuthStore } from '@/stores/auth';
  
  export default {
    setup() {
      const showContent = ref(false);
      const userStore = useUserStore();
      const authStore = useAuthStore();
  
      const profileToggle = () => {
        showContent.value = !showContent.value;
      };
      
      const logout = () => {
        authStore.logout();
      };

      onMounted(async () => {
        await userStore.getUserDetails();
      });
  
      return {
        showContent,
        profileToggle,
        logout,
        user: userStore
      };
    }
  }
  </script>
  
  <style scoped>
  .dropdown {
    position: absolute;
    top: 10px;
    right: 10px;
    padding: 5px;
    border-radius: 3px;
    z-index: 1;
  }

  .dropdown:hover,
  .dropdown.active {
  background-color: rgba(255, 255, 255, 0.1); 
}

  .user-info {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    cursor: pointer;
  }

    .user-info i {
        color: var(--white);
        font-size: 12px;
        margin-right: 5px;
    }

  .user-name {
    margin-right: 2px;
    color: var(--white);
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.5px;
    margin-right: 10px;
}

  .dropdown img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    border: -2px solid #aeaeae;
    cursor: pointer;
  }
  
  .dropdown-content {
    position: absolute;
    top: calc(100% + 5px);
    right: 0;
    min-width: 150px;
    padding: 10px;
    background-color: var(--blue);
    box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
    border-radius: 3px;
    z-index: 1;
  }
  
  .dropdown-content a {
    font-size: 15px;
    font-weight: 500;
    letter-spacing: 0.5px;
    color: var(--white);
    padding: 4px 10px;
    text-decoration: none;
    display: block;
  }

  .dropdown-content a:hover {
    text-decoration: underline;
  }
  </style>