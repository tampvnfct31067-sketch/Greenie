const API_KEY = "AIzaSyBFB8IB-u-6oEdes818EXPX0uR5eUDwkQA"; // Dán API key Google AI Studio vào đây

async function sendMessage() {
  const input = document.getElementById("userInput");
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim();
  if (!userMessage) return;

  chat.innerHTML += `<div class="message user">${userMessage}</div>`;
  input.value = "";

  const res = await fetch(
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [{ parts: [{ text: userMessage }] }]
      }),
    }
  );

  const data = await res.json();
  const botReply = data?.candidates?.[0]?.content?.parts?.[0]?.text || "Không có phản hồi.";
  chat.innerHTML += `<div class="message bot">${botReply}</div>`;
  chat.scrollTop = chat.scrollHeight;
}
