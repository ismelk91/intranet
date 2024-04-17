import { defineStore } from "pinia";
import router from "@/router";
import axios from "axios";
import { useUserStore } from "./user";
import { useTimeStore } from "./time";

export const useAuthStore = defineStore({
  id: "auth",
  state: () => ({
    user: localStorage.getItem("user") || "",
    token: localStorage.getItem("token") || "",
    role: localStorage.getItem("role") || "",
    returnUrl: "/",
  }),
  actions: {
    async login(email, password) {
      try {
        const response = await axios.post(
          "http://localhost:8080/api/v1/signin",
          {
            email,
            password,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        console.log(response.status);
        console.log(response.data.token);

        if (response.status === 200) {
          const token = response.data.token;
          const role = response.data.role;

          localStorage.setItem("user", email);
          localStorage.setItem("token", token);
          localStorage.setItem("role", role);
          this.user = email;
          this.token = token;
          this.role = role;

          const timeStore = useTimeStore();
          timeStore.clearTimeRegistrations();
          

          if (role === "ROLE_ADMIN") {
            if (this.role === "ROLE_ADMIN") {
              router.push("/admin");
            } else {
              console.error("User does not have access to the admin panel.");
            }
          } else {
            router.push(this.returnUrl || "/");
          }
          console.log("Logged-in user role:", this.role);
        }
      } catch (error) {
        console.error("Login failed:", error);
      }
    },
    async register(email, firstName, lastName, password, phoneNumber, birthDate) {
      try {
        const response = await axios.post(
          "http://localhost:8080/api/v1/register",
          {
            email,
            firstName,
            lastName,
            password,
            phoneNumber,
            birthDate,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        if (response.status === 200) {
          router.push("/login");
        }
      } catch (error) {
        console.error("Registration failed:", error);
      }
    },
    logout() {
      const userStore = useUserStore();
      userStore.logout();

      this.user = "";
      this.token = "";
      this.role = "";
      localStorage.removeItem("user");
      localStorage.removeItem("token");
      localStorage.removeItem("role");
      router.push("/login");
    },
    checkUserRole() {
      console.log("Logged-in user role:", this.role);
    },
  },
});