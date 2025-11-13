const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
const MODEL = "gemini-2.0-pro-exp-02-05";

// Danh sách từ khóa liên quan đến đề tài nghiên cứu
const allowedKeywords = [
  "giấy",
  "nảy mầm",
  "lục bình",
  "Eichhornia",
  "crassipes",
  "môi trường",
  "thân thiện",
  "tái chế",
  "xử lý lục bình",
  "nghiên cứu",
  "quy trình",
  "bền vững",
  "hạt giống",
  "giấy sinh học",
  "ứng dụng",
];

async function sendMessage() {
  const input = document.getElementById("userInput");
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim().toLowerCase();
  if (!userMessage) return;

  // Hiển thị tin nhắn người dùng
  chat.innerHTML += `<div class="message user">${userMessage}</div>`;
  input.value = "";

  // 🔒 Kiểm tra xem câu hỏi có liên quan đến đề tài không
  const isRelevant = allowedKeywords.some((keyword) =>
    userMessage.includes(keyword)
  );

  if (!isRelevant) {
    chat.innerHTML += `<div class="message bot">Xin lỗi, tôi chỉ có thể trao đổi về nội dung nghiên cứu giấy nảy mầm từ cây lục bình 🌿.</div>`;
    chat.scrollTop = chat.scrollHeight;
    return; // không gửi lên API
  }

  try {
    const res = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/${MODEL}:generateContent?key=${API_KEY}`,
      {
