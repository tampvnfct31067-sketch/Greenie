const API_KEY = "AIzaSyBFB8IB-u-6oEdes818EXPX0uR5eUDwkQA"; // Dán API key của bạn

async function sendMessage() {
  const input = document.getElementById("userInput");
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim();
  if (!userMessage) return;

  chat.innerHTML += `<div class="message user">${userMessage}</div>`;
  input.value = "";

  try {
    const res = await fetch(
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + API_KEY,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [
            {
              role: "user",
              parts: [{ text: userMessage }]
            }
          ]
        }),
      }
    );

    const data = await res.json();

    if (data.error) {
      chat.innerHTML += `<div class="message error">❌ Lỗi API: ${data.error.message}</div>`;
      console.error("Chi tiết lỗi:", data.error);
      return;
    }

    const botReply =
      data?.candidates?.[0]?.content?.parts?.[0]?.text ||
      "⚠️ Không có phản hồi từ chatbot (có thể API chưa trả kết quả).";
    chat.innerHTML += `<div class="message bot">${botReply}</div>`;
  } catch (error) {
    chat.innerHTML += `<div class="message error">❌ Lỗi kết nối: ${error.message}</div>`;
    console.error("Chi tiết lỗi:", error);
  }

  chat.scrollTop = chat.scrollHeight;
}
