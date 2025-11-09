
const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
const MODEL = "gemini-2.0-pro-exp-02-05";

document.addEventListener("DOMContentLoaded", () => {
  const sendBtn = document.getElementById("sendBtn");
  const input = document.getElementById("userInput");

  sendBtn.addEventListener("click", sendMessage);
  input.addEventListener("keypress", (e) => {
    if (e.key === "Enter") sendMessage();
  });
});

async function sendMessage() {
  const input = document.getElementById("userInput");
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim();
  if (!userMessage) return;

  chat.innerHTML += `<div class="message user">${userMessage}</div>`;
  input.value = "";

  // Hiển thị đang gửi
  const loadingMsg = document.createElement("div");
  loadingMsg.classList.add("message", "bot");
  loadingMsg.textContent = "⏳ Đang gửi...";
  chat.appendChild(loadingMsg);

  try {
    const res = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/${MODEL}:generateContent?key=${API_KEY}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [
            {
              role: "user",
              parts: [{ text: userMessage }]
            }
          ],
          system_instruction: {
            role: "system",
            parts: [
              {
                text: "Bạn là Greenie — chatbot AI nghiên cứu khoa học về giấy nảy mầm từ cây lục bình 🌱. Hãy trả lời thân thiện, dễ hiểu và không dùng ký hiệu Markdown."
              }
            ]
          }
        }),
      }
    );

    const data = await res.json();
    chat.removeChild(loadingMsg);

    if (data.error) {
      chat.innerHTML += `<div class="message error">❌ Lỗi API: ${data.error.message}</div>`;
      console.error("Chi tiết lỗi:", data.error);
      return;
    }

    const cleanText = (text) => {
      return text
        .replace(/\*\*(.*?)\*\*/g, '$1')
        .replace(/\*(.*?)\*/g, '$1')
        .replace(/_(.*?)_/g, '$1')
        .replace(/#+\s?(.*)/g, '$1');
    };

    const botReplyRaw =
      data?.candidates?.[0]?.content?.parts?.[0]?.text ||
      "⚠️ Không có phản hồi từ chatbot.";
    const botReply = cleanText(botReplyRaw);

    chat.innerHTML += `<div class="message bot">${botReply}</div>`;
  } catch (error) {
    chat.removeChild(loadingMsg);
    chat.innerHTML += `<div class="message error">❌ Lỗi kết nối: ${error.message}</div>`;
    console.error("Chi tiết lỗi:", error);
  }

  chat.scrollTop = chat.scrollHeight;
}
