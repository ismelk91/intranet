<template>
  <div class="content-wrapper" :class="{ 'weekly-view': view === 'weekly' }">

    <div class="calender-container">
      <div class="select-wrapper">
        <select v-model="view">
          <option value="weekly">Weekly</option>
          <option value="monthly">Monthly</option>
        </select>
      </div>

      <VDatePicker class="custom-calendar" v-model="time.date" :view="view" :min-date="oneYearBack" :max-date="today"
        trim-weeks borderless :attributes="attributes" @dayclick="handleDateChange" @did-move="handleCalendarMove">

        <template #day-popover>
          <div class="day-popover">
            <label>Time</label>
            <input type="number" v-model="time.workHours">
            <label>Project</label>
            <select v-model="selectedProject">
              <option value="null" disabled>Select Project</option>
              <option v-for="project in activeProjects" :key="project.id" :value="project.id">
                {{ project.project }}
              </option>
            </select>
            <div v-if="time.registrationExists" class="registration-message">
              <p>A time registration already exists for this project and date.</p>
            </div>
            <button @click="time.registerTime(selectedProject)" class="save-btn">Save</button>
          </div>
        </template>
      </VDatePicker>
    </div>

    <div class="time-container">
      <div v-if="time.totalTime !== null">
        <p class="date">{{ time.date }}</p>
      </div>

      <div v-if="selectedDateRegistrations.length > 0">
        <div class="heading">
          <p class="project-heading">project</p>
          <p class="today-heading">today</p>
          <p class="total-heading">total month</p>
        </div>
        <div class="time-registration">
          <ul>
            <li v-for="registration in selectedDateRegistrations" :key="registration.id">
              <span class="project">{{ getProjectName(registration.projectId) }}</span>
              <span class="hours">{{ registration.workHours }}h</span>
              <span class="total">{{ getTotalHoursForProjectPerMonth(registration.projectId) }}h</span>
            </li>
          </ul>
        </div>
        <p class="total-projects">All projects <span class="total-project-hours">{{ time.totalTime }}h</span></p>
      </div>

      <button v-if="hasTimeRegistrations" @click="updatePopover" class="update-btn">Update</button>

      <div class="update-popover" v-if="isUpdatePopupVisible">
        <i class="updateToggle bi bi-x" @click="updatePopover"></i>
        <ul>
          <li v-for="registration in selectedDateRegistrations" :key="registration.id">
            {{ getProjectName(registration.projectId) }}
            <input type="number" v-model="registration.workHours">

            <button class="savebtn" @click="time.updateTimeRegister(registration)">Save</button>
            <button class="deletebtn" @click="time.deleteTimeRegistration(registration.id)">Delete</button>
          </li>
          <p class="update-msg"> {{ time.updateMsg }}</p>
        </ul>

      </div>
    </div>
  </div>
</template>
  
<script setup>
import { ref, computed, onMounted } from 'vue';
import { useTimeStore } from '@/stores/time';
import { useProjectStore } from '@/stores/project';


const projects = useProjectStore();
const selectedProject = ref(null);
const isUpdatePopupVisible = ref(false);

const updatePopover = () => {
  isUpdatePopupVisible.value = !isUpdatePopupVisible.value;
  time.updateMsg = "";
};

const activeProjects = computed(() =>
  projects.projects.filter(project => project.active));

const time = useTimeStore();

const view = ref('monthly');

const today = new Date();

const oneYearBack = new Date(today.getFullYear(), today.getMonth() - 12, 1);
const oneMonthBack = new Date(today.getFullYear(), today.getMonth() - 1, 1);

const formatDate = date => {
  if (!(date instanceof Date)) return null;
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

time.date = formatDate(today);

const handleDateChange = (calendarDay) => {
  time.date = calendarDay.id;
  selectedProject.value = "null";
  time.workHours = '';
  time.registrationExists = false;
};

const attributes = computed(() => generateAttributesInRange(oneMonthBack, today));

const generateAttributesInRange = (start, end) => {
  const attributes = [];
  const registeredDates = time.timeRegistrations.map(registration => registration.date);

  for (let currentDate = new Date(start); currentDate <= end; currentDate.setDate(currentDate.getDate() + 1)) {
    const currentDateFormatted = formatDate(currentDate);
    const isRegistered = registeredDates.includes(currentDateFormatted);
    attributes.push({
      dates: new Date(currentDate),
      popover: {
        placement: 'bottom',
      },
      dot: {
        color: isRegistered ? 'red' : 'transparent',
      },
    });
  }
  return attributes;
};

const handleCalendarMove = (visiblePages) => {
  const calendarData = visiblePages[0];

  if (calendarData) {
    const month = calendarData.month;
    const year = calendarData.year;

    time.getTimeRegistrationsForSelectedMonth(year, month);
    time.getTotalTimeForSelectedMonth(year, month);
  }
};

const selectedDateRegistrations = computed(() => {
  const selectedDate = new Date(time.date);
  return time.timeRegistrations.filter(registration => {
    const registrationDate = new Date(registration.date);
    return isSameDay(selectedDate, registrationDate);
  });
});

const isSameDay = (date1, date2) => {
  return (
    date1.getFullYear() === date2.getFullYear() &&
    date1.getMonth() === date2.getMonth() &&
    date1.getDate() === date2.getDate()
  );
};

const getProjectName = (projectId) => {
  const project = projects.projects.find(p => p.id === projectId);
  return project ? project.project : "Unknown Project";
};

const hasTimeRegistrations = computed(() => selectedDateRegistrations.value.length > 0);

const getTotalHoursForProjectPerMonth = (projectId) => {
  const projectRegistrations = time.timeRegistrations.filter(registration => registration.projectId === projectId);

  const totalHours = projectRegistrations.reduce((total, registration) => {
    return total + registration.workHours;
  }, 0);

  return totalHours;
};

onMounted(() => {
  projects.getAllProjects();
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth() + 1;

  time.getTimeRegistrationsForSelectedMonth(year, month);
  time.getTotalTimeForSelectedMonth(year, month);
});

</script>

<style scoped>
.content-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin: auto;
  width: 1000px;
}

.select-wrapper {
  position: relative;
  margin-left: 550px;
}

.select-wrapper select {
  position: absolute;
  top: 20px;
  right: 100px;
  background-color: #223266;
  border: none;
  color: #fff;
  border-radius: 50px;
  padding: 3px 5px;
  z-index: 99;
}

.calender-container :deep(.vc-container) {
  display: flex;
  width: 700px;
  background-color: #111C44;
  border-right: 1px solid hsla(0, 0%, 100%, 0.5);
  border-bottom-left-radius: 9px;
  border-top-left-radius: 9px;
  z-index: 0;
}

.calender-container :deep(.vc-header) {
  margin-top: 20px;
  margin-bottom: 5px;
}

.calender-container :deep(.vc-title) {
  position: relative;
  color: #fff;
  text-transform: uppercase;
  font-size: 16px;
}

.calender-container :deep(.vc-base-icon) {
  color: #fff;
}

.calender-container :deep(.vc-title::after) {
  content: '';
  position: absolute;
  top: 50%;
  right: -20px;
  transform: translateY(-50%);
  width: 30px;
  height: 30px;
  background-image: url('../assets/arrow-down.svg');
  background-size: cover;
}

.calender-container :deep(.vc-nav-popover-container) {
  background-color: #223266;
  color: #fff;
  border: none;
  padding: 10px;
  margin-left: 22px;
}

.calender-container :deep(
.vc-nav-arrow,
.vc-nav-container,
.vc-nav-header,
.vc-nav-items,
.vc-nav-item) {
  background-color: #223266;
  color: #fff;
  border: none;
}

.calender-container :deep(.vc-nav-header button,
.vc-nav-items) {
  background-color: #223266;
  color: #fff;
}

.calender-container :deep(.vc-nav-item) {
  background-color: #111C44;
  color: #fff;
}

.calender-container :deep(.vc-container button) {
  background-color: #111C44;
  border: none;
  cursor: pointer;
  outline: none;
}

.calender-container :deep(.vc-container button:hover) {
  background-color: #111C44;
  color: #fff;
}

.calender-container :deep(.vc-weeks) {
  padding: 10px;
}

.calender-container :deep(.vc-weekday) {
  background-color: #111C44;
  color: #fff;
  padding: 0;
}

.calender-container :deep(.vc-popover-content) {
  background-color: #223266;
  color: #fff;
  border: none;
  padding: 10px;
}

.calender-container :deep(.vc-day) {
  padding: 0 5px 3px 5px;
  text-align: left;
  min-height: 65px;
  overflow: auto;
  background-color: #111C44;
  color: #fff;
}

.calender-container :deep(.vc-day.weekday-1,
.vc-day.weekday-7) {
  background-color: #111C44;
}

.calender-container :deep(.vc-highlight) {
  width: 35px;
  height: 35px;
}

.calender-container :deep(.vc-dots) {
  margin-bottom: 15px;
}

.day-popover {
  display: flex;
  flex-direction: column;
  width: 300px;
  min-height: 150px;
  padding: 5px;
  background-color: #223266;
  border: none;
}

.day-popover input {
  border-radius: 5px;
  outline: none;
}

.day-popover select {
  padding: 3px;
  border-radius: 5px;
}

.day-popover .save-btn {
  padding: 2px;
  width: 60px;
  background-color: #111C44;
  color: #fff;
  border-radius: 5px;
  border: 1px solid #111C44;
  position: absolute;
  right: 10px;
  bottom: 10px;
}

.day-popover .save-btn:hover {
  border: 1px solid #E69141;
}

.registration-message {
  width: 100%;
  margin: 5px 0 0 5px;
  font-size: 12px;
}

.time-container {
  background-color: #111C44;
  color: #fff;
  width: 280px;
  border-bottom-right-radius: 9px;
  border-top-right-radius: 9px;
  position: relative;
}

.time-container .heading {
  display: flex;
  text-align: center;
  font-size: 12px;
}

.time-container .project-heading {
  margin: 60px 0 0 30px;
}

.time-container .today-heading {
  margin: 60px 0 0 85px;
}

.time-container .total-heading {
  margin: 50px 35px 0;
}

.time-container ul {
  list-style: none;
  padding: 0;
}

.time-container .date {
  position: absolute;
  top: 10px;
  right: 25px;
}

.time-registration li {
  margin: 10px 0 0 30px;
  font-size: 14px;
}

.time-registration .hours {
  position: absolute;
  left: 160px;
  text-align: center;
}

.time-registration .total {
  position: absolute;
  right: 35px;
}

.time-container .total-projects {
  margin: 40px 0 0 30px;
  font-size: 14px;
}

.time-container .total-project-hours {
  position: absolute;
  right: 35px;
}

.update-btn {
  position: absolute;
  right: 10px;
  bottom: 10px;
  background-color: #223266;
  color: #fff;
  padding: 5px 20px;
  border-radius: 9px;
  border: none;
}

.update-btn:hover {
  background-color: orange;
}

.update-popover {
  display: flex;
  flex-direction: column;
  position: absolute;
  top: 140px;
  right: 260px;
  width: 450px;
  min-height: 165px;
  background-color: #223266;
  padding: 15px;
  z-index: 1000;
  border-radius: 9px;
  border: none;
}

.update-popover ul {
  margin-top: 10px;
}

.update-popover li {
  position: relative;
  display: flex;
  flex-direction: row;
  margin: 15px 0;
  font-size: 14px;
}

.update-popover input {
  position: absolute;
  left: 120px;
  border-radius: 5px;
  border: none;
  text-align: center;
  background-color: #f0f0f0;
  color: #141414;
  outline: none;
}

.update-popover .savebtn {
  background-color: #111C44;
  color: #fff;
  border-radius: 9px;
  border: none;
  padding: 2px 9px;
  position: absolute;
  right: 65px;
}

.update-popover .deletebtn {
  background-color: #111C44;
  color: #fff;
  border-radius: 9px;
  border: none;
  padding: 2px 9px;
  position: absolute;
  right: 0;
}

.updateToggle {
  position: absolute;
  top: 0;
  right: 0;
  height: 35px;
  width: 40px;
  font-size: 25px;
  color: #fff;
  cursor: pointer;
  transition: 0.3s;
  text-align: center;
}

.updateToggle:hover {
  background-color: #df3232;
  color: #fff;
  cursor: pointer;
}

.update-msg {
  margin: 0px;
  font-size: 14px;
}

.weekly-view {
  flex-direction: column;
  margin: 20px auto;
  width: 700px;
}

.weekly-view .calender-container :deep(.vc-container) {
  border-top-right-radius: 9px;
  border-bottom-left-radius: 0;
  border-bottom: 1px solid hsla(0, 0%, 100%, 0.5);
  border-right: none;
}

.weekly-view .time-container {
  width: 100%;
  background-color: #111C44;
  min-height: 150px;
  border-top-right-radius: 0;
  border-bottom-left-radius: 9px;
}

.weekly-view .time-container .date {
  right: 15px;
}

.weekly-view .time-container .project-heading {
  margin: 10px 0 5px 50px;
}

.weekly-view .time-container .today-heading {
  margin: 10px 0 5px 100px;
}

.weekly-view .time-container .total-heading {
  margin: 10px 0 5px 85px;
}

.weekly-view .time-registration li {
  margin: 5px 0 0 50px;
  font-size: 14px;
}

.weekly-view .time-registration .hours {
  position: absolute;
  left: 200px;
  text-align: center;
}

.weekly-view .time-registration .total {
  position: absolute;
  left: 330px;
}
.weekly-view .time-container .total-projects {
  margin: 20px 0 20px 50px;
  font-size: 14px;
}

.weekly-view .time-container .total-project-hours {
  position: absolute;
  left: 330px;
}

.weekly-view .update-popover {
  top: -90px;
  left: 150px;
  width: 400px;
  padding: 10px;
}

.weekly-view .update-popover ul {
  margin-top: 30px;
}

.weekly-view .update-popover li {
  margin: 15px 0 0 10px;
}

.weekly-view .update-popover input {
  width: 120px;
}
</style>