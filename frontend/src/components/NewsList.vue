<template>
    <div class="container">

        <div v-if="userStore.role === 'ROLE_USER' && !hideSearchAndToggleShowAll">
            <SearchBar class="search-bar" :search-query="searchQuery" @search="handleSearch"
                placeholder="Search for news.." />
        </div>

        <!-- <div v-if="userStore.role === 'ROLE_ADMIN' && !hideSearchAndToggleShowAll && displayedNewsList.length > 0"
            class="show-all-news-text" @click="toggleShowAllNews">
            {{ showAllNews ? 'Show less' : 'Show all' }}
        </div> -->

        <div>
            <div v-if="isEditFormVisible" class="edit-form">
                <i class="edit-toggle bi bi-x" @click="closeForm"></i>
                <form @submit.prevent="submitForm">
                    <input v-model="editedNews.subject" type="text" required />

                    <textarea v-model="editedNews.message" required></textarea>
                    <div class="button-container-edit-news">
                        <button type="submit" class="btn">Update News</button>
                        <button type="button" @click="deleteNewsItem" class="btn">Delete News</button>
                    </div>
                </form>
            </div>


            <div class="news-list-container">

                <div v-if="userStore.role === 'ROLE_USER' && displayedNewsList.length > 1" type="button"
                    class="show-oldest-news-text" @click="toggleSortNewestOldestNews">
                    {{ sortNewestOldestNews }}
                </div>

                <div v-for="(newsItem, index) in displayedNewsList" :key="newsItem.id">
                    <div v-if="showAllNews || index < 5" @click="openNewsDetailsPopup(newsItem)" class="news-item"
                        :class="{ 'highlighted': isNewsItemHovered === index }" @mouseover="highlightItem(index)"
                        @mouseleave="unhighlightItem">

                        <div class="news-content">
                            <div class="header">
                                <div class="news-item-subject">{{ newsItem.subject }}</div>
                            </div>
                            <div class="news-item-message">{{ newsItem.message }}</div>
                            <div v-if="newsItem.deadline" class="deadline">Deadline: {{ formatDate(newsItem.deadline) }}
                            </div>
                            <div class="date">Date: {{ formatDate(newsItem.date) }}</div>
                            <p v-if="allowEdit" class="edit-news-text" @click.stop="editNewsItem(newsItem)">Edit</p>
                        </div>
                    </div>
                </div>

            </div>

            <div v-if="selectedNewsItem" class="news-item-popup">
                <i class="news-item-toggle bi bi-x" @click="closeNewsDetailsPopup"></i>
                <div class="news-item-popup-content">
                    <div class="news-item-popup-subject">{{ selectedNewsItem.subject }}</div>
                    <div class="news-item-popup-date">Date: {{ formatDate(selectedNewsItem.date) }}</div>
                    <div class="news-item-popup-message">{{ selectedNewsItem.message }}</div>
                    <div class="news-item-popup-deadline">Deadline: {{ formatDate(selectedNewsItem.deadline) }}</div>
                </div>
                <div class="button-container">
                    <button @click="navigate('previous')" class="btn">Previous</button>
                    <button @click="navigate('next')" class="btn">Next</button>
                    <button @click="openSharePopup" class="btn">Share</button>
                </div>
            </div>
            <div v-if="showSharePopup" class="share-news-popup">
                <i class="share-toggle bi bi-x" @click="closeSharePopup"></i>
                <div class="share-news-popup-content">
                    <h3>Share news</h3>
                    <textarea v-model="shareText" placeholder="Enter email.."></textarea>
                </div>
                <div class="button-container">
                    <button @click="shareNews" class="btn submit-button">Submit</button>
                </div>
            </div>
        </div>

    </div>
    <Overlay v-if="isEditFormVisible || selectedNewsItem || showSharePopup" @close="closeForm" />
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue';
import { useNewsStore } from '@/stores/news';
import { useUserStore } from '@/stores/user';
import SearchBar from './SearchBar.vue';
import Overlay from './OverlayComponent.vue';
import axios from 'axios';

export default {
    props: {
        searchQuery: String,
        maxEntries: {
            type: Number,
            default: null,
        },
        allowEdit: {
            type: Boolean,
            default: true,
        },
        hideSearchAndToggleShowAll: {
            type: Boolean,
            default: false,
        }
    },
    setup(props) {
        const newsStore = useNewsStore();
        const userStore = useUserStore();

        const editedNews = ref({});
        const selectedNewsId = ref(null);
        const selectedNewsItem = ref(null);
        const isNewsItemHovered = ref(null);
        const searchQuery = ref('');
        const shareText = ref('');
        const showAllNews = ref(false);
        const isEditFormVisible = ref(false);
        const ascendingOrder = ref(false);
        const showSharePopup = ref(false);

        const sortNewestOldestNews = computed(() => (ascendingOrder.value ? 'Show lastest' : 'Show oldest'));

        const displayedNewsList = computed(() => {
            const sortedNewsList = newsStore.newsList
                .slice()
                .sort((a, b) =>
                    ascendingOrder.value
                        ? new Date(a.date) - new Date(b.date)
                        : new Date(b.date) - new Date(a.date)
                );

            const filteredList = sortedNewsList.filter((newsItem) =>
                newsItem.subject.toLowerCase().includes(searchQuery.value.toLowerCase())
            );

            return props.maxEntries ? filteredList.slice(0, props.maxEntries) : filteredList;
        });

        onMounted(() => {
            newsStore.getAllNews();
        });

        const toggleShowAllNews = () => {
            showAllNews.value = !showAllNews.value;
        };

        const toggleSortNewestOldestNews = () => {
            ascendingOrder.value = !ascendingOrder.value;
        };

        const isSearchVisible = computed(() => !props.hideSearch);
        const formatDate = (dateTime) => {
            if (!dateTime) {
                return '';
            }
            return new Date(dateTime).toLocaleDateString() || '';
        };

        const handleSearch = (query) => {
            searchQuery.value = query;
        };

        const highlightItem = (index) => {
            isNewsItemHovered.value = index;
        };

        const unhighlightItem = () => {
            isNewsItemHovered.value = null;
        };

        const openNewsDetailsPopup = (newsItem) => {
            selectedNewsItem.value = newsItem;
        };

        const closeNewsDetailsPopup = () => {
            selectedNewsItem.value = null;
        };

        const navigate = (direction) => {
            const currentIndex = newsStore.newsList.findIndex(
                (newsItem) => newsItem.id === selectedNewsItem.value.id
            );

            let newIndex;
            if (direction === 'previous') {
                newIndex = currentIndex > 0 ? currentIndex - 1 : newsStore.newsList.length - 1;
            } else {
                newIndex = currentIndex < newsStore.newsList.length - 1 ? currentIndex + 1 : 0;
            }

            selectedNewsItem.value = newsStore.newsList[newIndex];
        };

        const closeForm = () => {
            isEditFormVisible.value = false;
        };

        const submitForm = async () => {
            try {
                console.log('Submitting form with updated news:', editedNews.value);

                const response = await newsStore.updateNews(editedNews.value);

                console.log('Update news response:', response);
                await newsStore.getAllNews();
                isEditFormVisible.value = false;
            } catch (error) {
                console.error('Error updating news:', error);
            }
        };

        const deleteNewsItem = async () => {
            if (selectedNewsId.value) {
                await newsStore.deleteNews(selectedNewsId.value);
                await newsStore.getAllNews();
                isEditFormVisible.value = false;
            }
        };

        const editNewsItem = (newsItem) => {
            editedNews.value = { ...newsItem };
            selectedNewsId.value = newsItem.id;
            isEditFormVisible.value = !isEditFormVisible.value;
        };

        const openSharePopup = () => {
            showSharePopup.value = true;
        };

        const closeSharePopup = () => {
            showSharePopup.value = false;
        };

        const shareNews = async () => {
            const jwtToken = localStorage.getItem('token');
            if (!jwtToken) {
                console.error('JWT token not found in local storage.');
                return;
            }

            const requestBody = {
                email: shareText.value,
                newsDTO: selectedNewsItem.value
            };

            console.log('Request Payload:', requestBody);

            try {
                const response = await axios.post('http://localhost:8080/api/v1/admin/share-news', requestBody, {
                    headers: {
                        Authorization: `Bearer ${jwtToken}`
                    }
                });

                console.log('Response from server:', response.data);
                closeSharePopup();
            } catch (error) {
                console.error('Error sharing news:', error.response ? error.response.data : error.message);
            }
        };

        watch(() => props.searchQuery, (newQuery) => {
            handleSearch(newQuery);
        });

        watch(() => isEditFormVisible.value, () => {
            if (isEditFormVisible.value) {
                closeNewsDetailsPopup();
            }
        });

        return {
            editedNews,
            selectedNewsId,
            isEditFormVisible,
            editNewsItem,
            handleSearch,
            isSearchVisible,
            displayedNewsList,
            showAllNews,
            toggleShowAllNews,
            toggleSortNewestOldestNews,
            formatDate,
            isNewsItemHovered,
            highlightItem,
            unhighlightItem,
            openNewsDetailsPopup,
            closeNewsDetailsPopup,
            selectedNewsItem,
            navigate,
            deleteNewsItem,
            submitForm,
            closeForm,
            sortNewestOldestNews,
            showSharePopup,
            shareText,
            openSharePopup,
            closeSharePopup,
            shareNews,
            userStore,
        };
    },
    components: {
        SearchBar,
        Overlay,
    },
};
</script>


<style scoped>
.container {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.show-all-news-text {
    font-size: 22px;
    font-weight: 600;
    color: var(--white);
    text-transform: uppercase;
    cursor: pointer;
    z-index: 1;
}

.search-bar {
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
    margin-bottom: 30px;
    margin-top: 100px;
}

.search-bar::placeholder {
    color: var(--white);
}

.news-list-container {
    position: relative;
    padding: 20px;
}

.show-oldest-news-text {
    position: absolute;
    top: 0;
    right: 20px;
    font-size: 12px;
    font-weight: 600;
    color: var(--white);
    text-transform: uppercase;
    cursor: pointer;
    z-index: 1;
}

.show-oldest-news-text:hover {
    text-decoration: underline;
}

.news-item {
    display: flex;
    justify-content: space-between;
    width: 980px;
    height: 175px;
    background-color: var(--blue);
    color: var(--white);
    margin: auto;
    margin-bottom: 10px;
    border-radius: 9px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.news-item:hover {
    background-color: #111c446c;
}

.news-item-subject {
    margin: 20px auto;
    width: 95%;
    font-size: 20px;
    font-weight: bold;
}

.news-item-message {
    margin: 10px auto;
    width: 95%;
    min-height: 80px;
    font-size: 16px;
    line-height: 1.5em;
    word-wrap: break-word;
    text-overflow: ellipsis;
    overflow: hidden;
}

.date {
    position: absolute;
    bottom: 5px;
    left: 15px;
    font-size: 12px;
}

.deadline {
    position: absolute;
    bottom: 5px;
    right: 15px;
    font-size: 12px;
}

.news-item-popup {
    position: fixed;
    top: 50%;
    left: 50%;
    width: 500px;
    height: 400px;
    background-color: var(--blue);
    color: var(--white);
    transform: translate(-50%, -50%);
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    z-index: 9;
}

.news-item-popup-subject {
    font-size: 22px;
    text-align: center;
    font-weight: 600;
    margin-top: 30px;
}

.news-item-popup-message {
    font-size: 16;
    text-align: center;
    word-wrap: break-word;
    text-overflow: ellipsis;
    overflow: hidden;
    margin: 20px;
}

.news-item-popup-date {
    position: absolute;
    left: 100px;
    bottom: 50px;
    font-size: 12px;
}

.news-item-popup-deadline {
    position: absolute;
    right: 100px;
    bottom: 50px;
    font-size: 12px;
}

.news-item-toggle {
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

.news-item-toggle:hover {
    background-color: #df3232;
    color: #fff;
    cursor: pointer;
}

.news-content {
    flex: 1;
    position: relative;
    width: 300px;
}

.share-news-popup {
    position: fixed;
    top: 50%;
    left: 50%;
    width: 400px;
    height: auto;
    background-color: var(--blue);
    color: var(--white);
    transform: translate(-50%, -50%);
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
    z-index: 9;
}

.share-news-popup-content {
    top: 50%;
    left: 50%;
    height: 300px;
    background-color: var(--blue);
}

.share-news-popup-content textarea {
    width: 75%;
    height: 50px;
    margin-top: 60px;
    margin-left: 50px;
    padding: 5px;
    border-radius: 5px;
    outline: none;
}

.share-toggle {
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

.share-toggle:hover {
    background-color: #df3232;
    color: #fff;
    cursor: pointer;
}

.edit-news-text {
    position: absolute;
    top: 0;
    right: 0;
    text-transform: uppercase;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
}

.edit-news-text:hover {
    text-decoration: underline;
}

.edit-form {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background-color: var(--blue);
    color: var(--white);
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    z-index: 9;
}

.edit-form textarea {
    width: 100%;
    height: 200px;
    padding: 5px;
    margin-bottom: 15px;
    margin-top: 10px;
    font-size: 16px;
    outline: none;
}

.edit-form input {
    margin-top: 15px;
    width: 100%;
    padding: 5px;
    outline: none;
}

.edit-toggle {
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

.edit-toggle:hover {
    background-color: #df3232;
    color: #fff;
    cursor: pointer;
}

h3 {
    margin-top: 15px;
    margin-left: 125px;
    font-weight: 600;
    position: absolute;
    color: #ffffff;
    font-size: 22px;
    text-transform: uppercase;
}

h2,
p {
    color: #fff;
    margin-top: 10px;
    margin-right: 10px;
}

.button-container {
    position: absolute;
    bottom: 10px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    align-items: center;
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
  margin-right: 10px;
}

.btn:hover {
  background-color: var(--orange);
}

</style>
