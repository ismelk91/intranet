import axios from "axios";
import { useAuthStore } from "../stores/auth";

export const sendInvitations = async (emails, uuid) => {
  const auth = useAuthStore();
  const token = auth.token;

  try {
    const response = await axios.post(
      "http://localhost:8080/api/v1/admin/send-invitations",
      {
        emails: emails,
        uuid: uuid,
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    throw new Error("Error sending invitations:", error);
  }
};