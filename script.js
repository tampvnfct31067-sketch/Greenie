async function sendMessage() {
  const input = document.getElementById("user-input").value;
  const chat = document.getElementById("chat");
  if (!input) return;

  // Hiển thị tin nhắn người dùng
  chat.innerHTML += `<div class="user-msg">🧑‍💬 ${input}</div>`;
  document.getElementById("user-input").value = "";

  try {
    const response = await fetch(
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=AIzaSyBFB8IB-u-6oEdes818EXPX0uR5eUDwkQA",
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [
            {
              role: "user",
              parts: [
                {
                  text:
                    "Bạn là chatbot nghiên cứu khoa học hỗ trợ báo cáo đề tài 'Giấy nảy mầm từ cây lục bình'. Trả lời ngắn gọn, dễ hiểu, dùng tiếng Việt.\n\nCâu hỏi: " +
                    input,
                },
              ],
            },
          ],
        }),
      }
    );

    const data = await response.json();

    if (data.candidates && data.candidates[0].content.parts[0].text) {
      const reply = data.candidates[0].content.parts[0].text;
      chat.innerHTML += `<div class="bot-msg">🤖 ${reply}</div>`;
    } else {
      chat.innerHTML += `<div class="error">⚠️ Lỗi: ${JSON.stringify(data)}</div>`;
    }

    chat.scrollTop = chat.scrollHeight;
  } catch (error) {
    chat.innerHTML += `<div class="error">❌ Lỗi API: ${error}</div>`;
  }
}
