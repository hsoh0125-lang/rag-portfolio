import anthropic
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, ConfigDict, Field

load_dotenv()
client = anthropic.Anthropic()

app = FastAPI()


class AskRequest(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)
    question: str = Field(min_length=1, max_length=500)


@app.get("/hello")
def hello():
    return {"message": "hello"}


@app.post("/ask")
def ask(request: AskRequest):
    try:
        message = client.messages.create(
            model="claude-haiku-5-5",
            max_tokens=300,
            messages=[{"role": "user", "content": request.question}],
        )
    except anthropic.APIError as e:
        print("LLM 오류:", type(e).__name__, getattr(e, "status_code", None), getattr(e, "message", ""))
        raise HTTPException(status_code=502, detail="LLM 호출에 실패했어요")
    return {"answer": message.content[0].text}
