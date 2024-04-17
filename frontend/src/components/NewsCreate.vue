<template>
    <div v-if="userRole === 'ROLE_ADMIN'" class="create-news-text" @click="toggleFormVisibility">Create news</div>
    <div v-else>
        <p>You do not have permission to create news articles.</p>
    </div>
    <div v-if="isFormVisible" class="create-news">
        <i class="toggle-news bi bi-x" @click="toggleFormVisibility"></i>
        <form @submit.prevent="createNews">
            <label>Date</label>
            <input v-model="newNews.date" type="datetime-local" required>
            <label>Deadline</label>
            <input v-model="newNews.deadline" type="datetime-local">
            <label>Subject</label>
            <input v-model="newNews.subject" type="text" required>
            <label>Message</label>
            <textarea v-model="newNews.message" required></textarea>
            <button type="submit" class="create-news-btn btn btn-primary">Create News</button>
        </form>
    </div>
    <Overlay v-if="isFormVisible" @close="toggleFormVisibility" />
</template>

<script>
import { ref, getCurrentInstance, watch } from 'vue';
import { useNewsStore } from '@/stores/news';
import { useAuthStore } from '@/stores/auth';
import Overlay from './OverlayComponent.vue';

export default {
    setup() {
        const newsStore = useNewsStore();
        const newNews = ref({ subject: '', message: '', date: formatDate(new Date()), deadline: formatDeadline(new Date()) });
        const isFormVisible = ref(false);

        const { emit } = getCurrentInstance();
        const authStore = useAuthStore();
        const userRole = ref(authStore.role);

        watch(() => authStore.role, (newRole) => {
            userRole.value = newRole;
        });

        const createNews = async () => {
            try {
                const createdNews = await newsStore.createNews(newNews.value);
                console.log('Created news:', createdNews.data);

                await newsStore.getAllNews();

                emit('news-created');
                newNews.value = { subject: '', message: '', date: '', deadline: '' };
            } catch (error) {
                console.error('Error creating news:', error);
            }
        };

        function formatDate(date) {
            const year = date.getFullYear();
            let month = date.getMonth() + 1;
            let day = date.getDate();
            let hours = date.getHours();
            let minutes = date.getMinutes();

            if (month < 10) {
                month = '0' + month;
            }
            if (day < 10) {
                day = '0' + day;
            }
            if (hours < 10) {
                hours = '0' + hours;
            }
            if (minutes < 10) {
                minutes = '0' + minutes;
            }

            return `${year}-${month}-${day}T${hours}:${minutes}`;
        }

        function formatDeadline(date) {
            const year = date.getFullYear();
            let month = date.getMonth() + 1;
            let day = date.getDate();
            let hours = date.getHours();
            let minutes = date.getMinutes();

            if (month < 10) {
                month = '0' + month;
            }
            if (day < 10) {
                day = '0' + day;
            }
            if (hours < 10) {
                hours = '0' + hours;
            }
            if (minutes < 10) {
                minutes = '0' + minutes;
            }

            return `${year}-${month}-${day}T${hours}:${minutes}`;
        }

        const toggleFormVisibility = () => {
            isFormVisible.value = !isFormVisible.value;
        };

        return {
            newNews,
            createNews,
            userRole,
            isFormVisible,
            toggleFormVisibility,
        };
    },
    components: {
        Overlay
    },
};
</script>

<style scoped>
.create-news-text {
    position: absolute;
    top: 180px;
    padding-left: 40px;
    font-size: 22px;
    font-weight: 600;
    text-transform: uppercase;
    color: var(--white);
    cursor: pointer;
    z-index: 1;
}

.create-news {
    position: fixed;
    top: 35%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 16px;
    min-height: 260px;
    width: 450px;
    background: #111C44;
    color: #fff;
    padding: 20px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    z-index: 9;
}

.create-news-btn {
    margin: 10px auto;
    width: 100%;
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
}

.create-news-btn:hover {
    background-color: var(--orange);
}

.create-news label {
    display: block;
    margin-top: 10px;
}

.create-news input {
    width: 100%;
    height: 40px;
    padding: 5px;
    border-radius: 5px;
    outline: none;
}

.create-news textarea {
    min-height: 70px;
    width: 100%;
    border-radius: 5px;
    outline: none;
}

.toggle-news {
    position: absolute;
    display: flex;
    justify-content: center;
    align-items: center;
    top: 0;
    right: 0;
    height: 30px;
    width: 30px;
    color: #fff;
    font-size: 25px;
    cursor: pointer;
    transition: 0.3s;
    border-top-right-radius: 5px;
}

.toggle-news:hover {
    background-color: #df3232;
    color: #fff;
    cursor: pointer;
}
</style>