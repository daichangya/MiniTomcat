let seconds = 0;
let timer;

function startTimer() {
  if (!timer) {
    timer = setInterval(() => {
      seconds++;
      updateTimerDisplay();
    }, 1000);
  }
}

function stopTimer() {
  clearInterval(timer);
  timer = null;
}

function resetTimer() {
  clearInterval(timer);
  seconds = 0;
  updateTimerDisplay();
  timer = null;
}

function updateTimerDisplay() {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const remainingSeconds = seconds % 60;

  const formattedTime = `${padZero(hours)}:${padZero(minutes)}:${padZero(remainingSeconds)}`;
  document.getElementById('timer').innerHTML = formattedTime;
}

function padZero(num) {
  return num.toString().padStart(2, '0');
}

// 页面加载时绑定事件监听器
window.onload = function () {
  document.getElementById('startButton').addEventListener('click', startTimer);
  document.getElementById('resetButton').addEventListener('click', resetTimer);
  document.getElementById('stopButton').addEventListener('click', stopTimer);
};