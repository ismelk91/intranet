<template>
    <div @click="togglePopup" class="invite-text">Invite users</div>

    <div>
        <div v-if="isPopupVisible" class="popup">
            <div class="popup-content">
                <i class="invite-toggle bi bi-x" @click="togglePopup"></i>
                <h2>Invite Users</h2>
                <form @submit.prevent="inviteUsers">
                    <div v-for="(email, index) in emails" :key="index">
                        <input v-model="emails[index]" type="email" placeholder="email" />
                    </div>
                    <div class="button-container-invite-users">
                        <button @click.prevent="addEmail" class="add-email-button">Add Another Email</button>
                        <button @click.prevent="removeLastEmail" v-if="emails.length > 1"
                            class="remove-email-button">Remove Last Email</button>
                        <button type="submit" class="submit-button">Send Invitations</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <Overlay v-if="isPopupVisible" @close="togglePopup" />
</template>

<script>
import { sendInvitations } from '../stores/invitation';
import Overlay from './OverlayComponent.vue';

export default {
    name: 'InvitationForm',
    components: {
        Overlay,
    },
    data() {
        return {
            isPopupVisible: false,
            emails: [''],
        };
    },
    methods: {
        togglePopup() {
            this.isPopupVisible = !this.isPopupVisible;
        },
        async inviteUsers() {
            const validEmails = this.emails.filter(email => email.trim() !== '');
            console.log('Valid emails:', validEmails);

            if (validEmails.length > 0) {
                try {
                    await sendInvitations(validEmails, this.uuid);
                    this.togglePopup();
                } catch (error) {
                    console.error('Error sending invitations:', error);
                }
            } else {
                console.error('No valid emails to send invitations.');
            }
        },
        addEmail() {
            this.emails.push('');
        },
        removeLastEmail() {
            if (this.emails.length > 1) {
                this.emails.pop();
            }
        },
    },
};
</script>

<style scoped>
.invite-text {
    position: absolute;
    top: 250px;
    padding-left: 40px;
    font-size: 22px;
    font-weight: 600;
    text-transform: uppercase;
    color: var(--white);
    cursor: pointer;
    z-index: 1;
}

.popup {
    position: absolute;
    top: 50%;
    left: 50%;
    padding: 15px;
    transform: translate(-50%, -50%);
    background-color: var(--blue);
    color: var(--white);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    z-index: 9;
}

.popup-content {
    text-align: center;
}

.add-email-button,
.remove-email-button,
.submit-button {
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

.add-email-button:hover,
.remove-email-button:hover,
.submit-button:hover {
    background-color: var(--orange);
}

input {
    margin-bottom: 15px;
    padding: 8px;
    width: 100%;
    height: 40px;
    border: 1px solid var(--blue);
    font-size: 16px;
    outline: none;
}

h2 {
    font-size: 26px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 2px;
}

.invite-toggle {
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

.invite-toggle:hover {
    background-color: #df3232;
    color: #fff;
    cursor: pointer;
}
</style>