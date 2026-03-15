import { useState } from "react"
import { Button, Form } from "react-bootstrap"
import useFetch from "../../hooks/useFetch"
import { toast } from "react-toastify"

const AddQuiz = ({ lessonID }) => {
  const { post } = useFetch()
  const [question, setQuestion] = useState("")

  const normalizeText = (value) => {
    if (typeof value !== "string") return value
    return value.normalize("NFC")
  }

  const getUtf8Headers = () => {
    const token = localStorage.getItem("token")
    return {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json; charset=UTF-8",
      "Accept-Charset": "UTF-8",
    }
  }

  const handleAddQuiz = async () => {
    if (!question.trim()) return toast.error("Question required")

    const payload = {
      question: normalizeText(question),
      type: "SINGLE_CHOICE",
      options: [
        { content: "Option A", isCorrect: true },
        { content: "Option B", isCorrect: false }
      ]
    }

    await post(
      payload,
      getUtf8Headers(),
      `http://localhost:8080/api/quizzes/lesson/${lessonID}`
    )

    toast.success("Quiz added")
    setQuestion("")
  }

  return (
    <div className="mt-3">
      <Form.Control
        placeholder="Quiz question"
        value={question}
        onChange={(e) => setQuestion(e.target.value)}
        className="mb-2"
      />
      <Button size="sm" onClick={handleAddQuiz}>
        + Add Quiz
      </Button>
    </div>
  )
}

export default AddQuiz