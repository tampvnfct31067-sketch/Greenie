const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
async function sendMessage() {
  const input = document.getElementById("user-input");
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim();
  if (!userMessage) return;

  chat.innerHTML += `<div class="message user">${userMessage}</div>`;
  input.value = "";

  try {
    const res = await fetch(
      "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-pro-exp-02-05:generateContent?key=" + API_KEY,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [{ role: "user", parts: [{ text: userMessage }] }],
          generationConfig: { temperature: 0.7, topP: 0.9 }
        }),
      }
    );

    const data = await res.json();

    // Kiểm tra lỗi 503 và tự động thử lại
    if (data.error?.code === 503) {
      chat.innerHTML += `<div class="message error">⚠️ Máy chủ quá tải. Đang thử lại sau 5 giây...</div>`;
      console.warn("Máy chủ quá tải, thử lại sau...");
      setTimeout(sendMessage, 5000);
      return;
    }

    if (data.error) {
      chat.innerHTML += `<div class="message error">❌ Lỗi API: ${data.error.message}</div>`;
      console.error("Chi tiết lỗi:", data.error);
      return;
    }

    const botReply =
      data?.candidates?.[0]?.content?.parts?.[0]?.text ||
      "⚠️ Không có phản hồi từ chatbot.";
    chat.innerHTML += `<div class="message bot">${botReply}</div>`;
  } catch (error) {
    chat.innerHTML += `<div class="message error">❌ Lỗi kết nối: ${error.message}</div>`;
    console.error("Chi tiết lỗi:", error);
  }

  chat.scrollTop = chat.scrollHeight;
}
