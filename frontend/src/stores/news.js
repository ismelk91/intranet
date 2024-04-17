import { defineStore } from 'pinia';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

export const useNewsStore = defineStore({
    id: 'news',
    state: () => ({
        newsList: [],
        editingNews: null,
    }),
    actions: {
        async createNews(newsData) {
            try {
                const authStore = useAuthStore();
                const token = authStore.token;

                const response = await axios.post('http://localhost:8080/api/v1/create-news', newsData, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                return response;
            } catch (error) {
                console.error('Error creating news:', error);
                throw error;
            }
        },
        async getAllNews() {
            try {
                const authStore = useAuthStore();
                const token = authStore.token;

                const response = await axios.get('http://localhost:8080/api/v1/get-news', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                this.newsList = response.data;
                return response;
            } catch (error) {
                console.error('Error fetching news:', error);
                throw error;
            }
        },
        async deleteNews(newsId) {
            try {
                if (!newsId) {
                    console.error('Invalid newsId:', newsId);
                    return;
                }

                const authStore = useAuthStore();
                const token = authStore.token;

                const response = await axios.delete(`http://localhost:8080/api/v1/delete-news`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                    params: {
                        id: newsId
                    },
                });

                console.log('Delete news response:', response);

                return response;
            } catch (error) {
                console.error('Error deleting news:', error);
                throw error;
            }
        },

        async updateNews(newsData) {
            try {
                const authStore = useAuthStore();
                const token = authStore.token;

                const response = await axios.put(`http://localhost:8080/api/v1/edit-news/${newsData.id}`, newsData, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                

                return response;
            } catch (error) {
                console.error('Error updating news:', error);
                throw error;
            }
        },
        setEditingNews(newsItem) {
            this.editingNews = newsItem;
        }
    },
});

export function setupNewsStore() {
    return useNewsStore();
}
