<template>
    <div class="user-profile-container">
        <div class="top-left">
            <router-link v-if="userRole === 'ROLE_ADMIN'" to="/admin">
                <button class="back-btn btn btn-primary"><i class="bi bi-chevron-left"></i> Back</button>
            </router-link>
        </div>
        <span v-if="!editMode" class="edit-text" @click="openEditPopup">Edit profile</span>
        <div class="content-container">

            <label for="fileInput" class="img-container">
                
                <img v-if="user.profilePictureUrl" :src="user.profilePictureUrl" :alt="user.firstName + user.lastName + ' Profile Picture'" />
                <img v-else src="../assets/no-profile-picture.png" alt="No Profile Picture" />

                <div class="user-name">
                    {{ user.firstName }} {{ user.lastName }}
                </div>
                <div>
                    <button class="add-photo-btn btn-primary" @click="openPopup">
                        {{ user.profilePictureUrl ? 'Change Photo' : 'Add Photo' }}
                    </button>
                </div>
            </label>

            <div class="popup" v-if="showPopup">
                <i class="upload-toggle bi bi-x" @click="openPopup"></i>
                <h6>Change Photo</h6>
                <div class="image-preview-container">
                    <img :src="imagePreviewUrl" class="image-preview" />
                </div>
                <input id="fileInput" type="file" style="display: none;" ref="fileInput" @change="handleFileChange" />

                <div class="add-delete-container">
                    <span class="add-photo-text" @click="openFileInput">
                        <i class="bi bi-cloud-upload"></i>
                        {{ user.profilePictureUrl ? 'Change photo' : 'Add Photo' }}
                    </span>
                    <span :class="{ disabled: !user.profilePictureUrl }" class="delete-text"
                        @click="deleteProfilePicture">
                        <i class="bi bi-trash3"></i> Delete photo
                    </span>
                </div>

                <div class="popup-buttons-container">
                    <button class="upload-btn btn-primary" @click="uploadProfilePicture">
                        Save
                    </button>
                    <button class="cancel-btn btn-primary" @click="openPopup">Cancel</button>
                </div>
            </div>

            <div class="form-container">
                <div class="row mb-3">
                    <div class="label">
                        <h6 class="mb-0">First name</h6>
                    </div>
                    <div class="value text-secondary">
                        <input type="text" class="form-control" v-model="editedFirstName" :disabled="!editMode">
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="label">
                        <h6 class="mb-0">Last name</h6>
                    </div>
                    <div class="value text-secondary">
                        <input type="text" class="form-control" v-model="editedLastName" :disabled="!editMode">
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="label">
                        <h6 class="mb-0">Birthdate</h6>
                    </div>
                    <div class="value text-secondary">
                        <input type="date" class="form-control" v-model="editedBirthDate" :disabled="!editMode">
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="label">
                        <h6 class="mb-0">Email</h6>
                    </div>
                    <div class="value text-secondary">
                        <input type="text" class="form-control" :value="user.email" disabled>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="label">
                        <h6 class="mb-0">Phone number</h6>
                    </div>
                    <div class="value text-secondary">
                        <input type="text" class="form-control" v-model="editedPhoneNumber" :disabled="!editMode">
                    </div>
                </div>

                <div class="edit-popup" v-if="showEditPopup">
                    <h6>Profile details</h6>
                    <div class="edit-popup-content">
                        <i class="edit-toggle bi bi-x" @click="openEditPopup"></i>
                        <div class="row mb-3">
                            <div class="label">
                        <h6 class="mb-0">First name</h6>
                    </div>
                            <div class="value text-secondary">
                                <input type="text" class="edit-input form-control" v-model="editedFirstName"  placeholder="first name">
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="label">
                        <h6 class="mb-0">Last name</h6>
                    </div>
                            <div class="value text-secondary">
                                <input type="text" class="edit-input form-control" v-model="editedLastName">
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="label">
                        <h6 class="mb-0">Birthdate</h6>
                    </div>
                            <div class="value text-secondary">
                                <input type="date" class="edit-input form-control" v-model="editedBirthDate">
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="label">
                        <h6 class="mb-0">Email</h6>
                    </div>
                            <div class="value text-secondary">
                                <input type="text" class="edit-input form-control" :value="user.email" disabled>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="label">
                        <h6 class="mb-0">Phone number</h6>
                    </div>
                            <div class="value text-secondary">
                                <input type="text" class="edit-input form-control" v-model="editedPhoneNumber">
                            </div>
                        </div>
                        <div class="edit-btns">
                            <button class="save-btn btn-primary" @click="saveChanges">Save</button>
                            <button class="cancel-btn btn-primary" @click="openEditPopup">Cancel</button>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>

     <Overlay v-if="showPopup" @close-overlay="openPopup" />
     <Overlay v-if="showEditPopup" @close-overlay="openEditPopup" />
</template>

<script>
import { onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/user';
import Overlay from './OverlayComponent.vue';
import { useAuthStore } from '@/stores/auth';

export default {
    components: {
        Overlay,
    },
    setup() {
        const userStore = useUserStore();
        const authStore = useAuthStore();
        const editedPhoneNumber = ref(userStore.phoneNumber);
        const editedFirstName = ref(userStore.firstName);
        const editedLastName = ref(userStore.lastName);
        const editedBirthDate = ref(userStore.birthDate);
        const fileInput = ref(null);
        const selectedFile = ref(null);
        const imagePreviewUrl = ref(null);
        const showPopup = ref(false);
        const showEditPopup = ref(false);
        const editMode = ref(false);

        const openPopup = () => {
            showPopup.value = !showPopup.value;
        };

        const openEditPopup = () => {
            showEditPopup.value = !showEditPopup.value;
        };

        const getUserProfile = async () => {
            try {
                await userStore.getUserDetails();
                editedPhoneNumber.value = userStore.phoneNumber;
                editedFirstName.value = userStore.firstName;
                editedLastName.value = userStore.lastName;
                editedBirthDate.value = userStore.birthDate;
                imagePreviewUrl.value = userStore.profilePictureUrl;
            } catch (error) {
                console.error('Error fetching user details:', error.response || error.message);
            }
        };

        // Handle file change and preview image
        const handleFileChange = () => {
            const file = fileInput.value.files[0];
            if (file && file.type.startsWith('image/')) {
                selectedFile.value = file;
                const reader = new FileReader();
                reader.onload = (e) => {
                    imagePreviewUrl.value = e.target.result;
                };
                reader.readAsDataURL(file);
            } else {
                console.error('Please select a valid image file.');
            }
        };

        const uploadProfilePicture = async () => {
            if (!selectedFile.value) {
                console.error('Please select a file.');
                return;
            }

            try {
                await userStore.uploadProfilePictureWithSasToken(selectedFile.value);
                window.location.reload();
            } catch (error
            ) {
                console.error('Error uploading profile picture:', error);
            }
        };
        const deleteProfilePicture = async () => {
            try {
                await userStore.deleteProfilePicture();
                imagePreviewUrl.value = '../assets/no-profile-picture.png';
            } catch (error) {
                console.error('Error deleting profile picture:', error);
            }
        };

        const openFileInput = () => {
            fileInput.value.click();
        };

        const saveChanges = async () => {
            if (editedPhoneNumber.value !== userStore.phoneNumber) {
                await updatePhoneNumber();
            }
            if (editedFirstName.value !== userStore.firstName) {
                await updateFirstName();
            }
            if (editedLastName.value !== userStore.lastName) {
                await updateLastName();
            }
            if (editedBirthDate.value !== userStore.birthDate) {
                await updateBirthDate();
            }
            openEditPopup();
        };

        const updatePhoneNumber = async () => {
            try {
                await userStore.updatePhoneNumber(editedPhoneNumber.value);
            } catch (error) {
                console.error('Error updating phone number:', error);
            }
        };

        const updateFirstName = async () => {
            try {
                await userStore.updateFirstName(editedFirstName.value);
            } catch (error) {
                console.error('Error updating first name:', error);
            }
        };

        const updateLastName = async () => {
            try {
                await userStore.updateLastName(editedLastName.value);
            } catch (error) {
                console.error('Error updating last name', error);
            }
        };

        const updateBirthDate = async () => {
            try {
                await userStore.updateBirthDate(editedBirthDate.value);
            } catch (error) {
                console.error('Error updating birthdate:', error);
            }
        };

        onMounted(() => {
            getUserProfile();
        });

        return {
            user: userStore,
            editedPhoneNumber,
            editedFirstName,
            editedLastName,
            editedBirthDate,
            fileInput,
            imagePreviewUrl,
            showPopup,
            showEditPopup,
            editMode,
            openPopup,
            openEditPopup,
            handleFileChange,
            uploadProfilePicture,
            deleteProfilePicture,
            openFileInput,
            saveChanges,
            updatePhoneNumber,
            updateFirstName,
            updateLastName,
            updateBirthDate,
            userRole: authStore.role,
        };
    },
};
</script>

<style scoped>
.user-profile-container {
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin: 100px auto;
    background-color: var(--blue);
    color: var(--white);
    width: 980px;
    height: 500px;
    border-radius: 9px;
    padding: 20px;
}

.top-left {
    position: absolute;
    top: 15px;
    left: 15px;
}

.back-btn {
    background-color: #223266;
    color: var(--white);
    padding: 6px 10px;
    border: none;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 1.5px;
    text-transform: uppercase;
}

.back-btn:hover {
    background-color: var(--orange);
}

.content-container {
    display: flex;
    width: 100%;
}

.img-container {
    width: 30%;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-right: 40px;
}

.img-container img {
    width: 150px;
    height: 150px;
    border-radius: 50%;
    margin-bottom: 30px;
}

.user-name {
    font-size: 22px;
    font-weight: 400;
    letter-spacing: 1.5px;
}

.add-photo-btn {
    background-color: #223266;
    color: var(--white);
    padding: 8px 15px;
    border-radius: 50px;
    border: none;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 1.5px;
    text-transform: uppercase;
}

.add-photo-btn {
    margin-top: 30px;
    margin-right: 10px;
}

.add-photo-btn:hover {
    background-color: var(--orange);
}

.form-container {
    flex: 1;
    margin-left: 50px;
    width: 70%;
    text-transform: uppercase;
}

.form-control {
    width: calc(100% - 40px);
    padding: 5px 10px;
    background-color: var(--blue);
    border: 1px solid var(--white);
    color: var(--white);
}

.label {
    margin-bottom: 5px;
}

.edit-btns {
    position: absolute;
    bottom: 20px;
    right: 20px;
}

.popup {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 300px;
    height: 450px;
    padding: 20px;
    transform: translate(-50%, -50%);
    background-color: var(--blue);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    z-index: 9;
    border-radius: 5px;
}

.image-preview-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
    margin-bottom: 20px;
}

.image-preview {
    width: 180px;
    height: 180px;
    border-radius: 50%;
    background-color: rgb(183, 183, 183);
}

.add-delete-container {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    width: 100%;
    margin-top: 40px;
    font-size: 14px;
    letter-spacing: 0.5px;
}

.add-photo-text,
.delete-text {
    cursor: pointer;
    display: inline-block;
    margin-bottom: 5px;
}

.disabled {
    color: #999;
    cursor: none;
    pointer-events: none;
}

.bi-cloud-upload,
.bi-trash3 {
    font-size: 12px;
}

.popup-buttons-container {
    position: absolute;
    bottom: 10px;
    right: 10px;
}

.upload-btn {
    margin-right: 5px;
}
.save-btn {
    margin-right: 5px;
}

.cancel-btn,
.save-btn,
.upload-btn
 {
    background-color: #223266;
    color: var(--white);
    padding: 6px 10px;
    border-radius: 5px;
    border: none;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 1.5px;
    text-transform: uppercase;
}

.cancel-btn:hover,
.save-btn:hover,
.upload-btn:hover {
    background-color: var(--orange);
}


.upload-toggle {
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

.upload-toggle:hover {
    background-color: #df3232;
    color: #fff;
    cursor: pointer;
}

.edit-btn {
    background-color: #223266;
    padding: 8px 15px;
    border-radius: 9px;
    border: none;
}

.edit-btn:hover {
    background-color: var(--orange);
}

.edit-popup {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 650px;
    height: 510px;
    padding: 20px;
    transform: translate(-50%, -50%);
    background-color: var(--blue);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    z-index: 9;
    border-radius: 5px;
}

.edit-text {
    position: absolute;
    top: 20px;
    right: 20px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 1.5px;
}

.edit-text:hover {
    text-decoration: underline;
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

.edit-popup-content {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 10px;
}

.edit-input {
    width: 100%;
    background-color: var(--white);
    color: black;
}

.edit-input[disabled] {
    background-color: #ccc; 
    color: #555; 
}

</style>