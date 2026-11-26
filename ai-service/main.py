from fastapi import FastAPI
from pydantic import BaseModel, ConfigDict, Field

app = FastAPI()


class AskRequest(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)
    question: str = Field(min_length=1, max_length=500)


@app.get("/hello")
def hello():
    return {"message": "hello"}


@app.post("/ask")
def ask(request: AskRequest):
    return {"question": request.question}
