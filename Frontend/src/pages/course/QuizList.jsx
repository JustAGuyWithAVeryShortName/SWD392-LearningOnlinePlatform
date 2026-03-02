import { useEffect, useState } from "react"
import useFetch from "../../hooks/useFetch"

const QuizList = ({ lessonID }) => {
  const { get } = useFetch()
  const [quizzes, setQuizzes] = useState([])

  useEffect(() => {
    if (!lessonID) return

    const fetchQuizzes = async () => {
      const data = await get(
        `http://localhost:8080/api/quizzes/lesson/${lessonID}`
      )
      setQuizzes(data || [])
    }

    fetchQuizzes()
  }, [lessonID])

  return (
    <div>
      {quizzes.length === 0 && <p>No quiz yet</p>}

      {quizzes.map((q, index) => (
        <div key={q.quizId} className="border p-3 mb-2 rounded">
          <b>Q{index + 1}:</b> {q.question}
        </div>
      ))}
    </div>
  )
}

export default QuizList