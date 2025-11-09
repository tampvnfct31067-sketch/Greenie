document.addEventListener("DOMContentLoaded", () => {
  const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
  const MODEL = "gemini-2.0-pro-exp-02-05";

  const input = document.getElementById("userInput");
  const chat = document.getElementById("chat");
  const sendBtn = document.getElementById("sendBtn");

  async function sendMessage() {
    const userMessage = input.value.trim();
    if (!userMessage) return;

    chat.innerHTML += `<div class="user-msg">${userMessage}</div>`;
    input.value = "";

    try {
      const res = await fetch(
        `https://generativelanguage.googleapis.com/v1/models/${MODEL}:generateContent?key=${API_KEY}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            contents: [{ role: "user", parts: [{ text: userMessage }] }],
            system_instruction: {
              role: "system",
              parts: [
                {
                  text: "Bạn là Greenie 🌱 — chatbot hỗ trợ nghiên cứu khoa học về giấy nảy mầm từ cây lục bình. Hãy trả lời thân thiện, rõ ràng, không dùng dấu *.",
                },
              ],
            },
          }),
        }
      );

      const data = await res.json();

      if (data.error) {
        chat.innerHTML += `<div class="error">❌ Lỗi API: ${data.error.message}</div>`;
        console.error("Chi tiết lỗi:", data.error);
        return;
      }

      const botReply =
        data?.candidates?.[0]?.content?.parts?.[0]?.text ||
        "⚠️ Không có phản hồi từ chatbot.";
      chat.innerHTML += `<div class="bot-msg">${botReply}</div>`;
    } catch (error) {
      chat.innerHTML += `<div class="error">❌ Lỗi kết nối: ${error.message}</div>`;
      console.error("Chi tiết lỗi:", error);
    }

    chat.scrollTop = chat.scrollHeight;
  }

  // Gắn sự kiện
  sendBtn.addEventListener("click", sendMessage);
  input.addEventListener("keypress", (e) => {
    if (e.key === "Enter") sendMessage();
  });
});
