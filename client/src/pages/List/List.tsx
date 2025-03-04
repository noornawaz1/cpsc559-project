import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import styles from "./List.module.scss";

interface Task {
  id: number;
  name: string;
  isComplete: boolean;
}

interface ListResponse {
  name: string;
  author: string;
  tasks: Task[];
}

function List() {
  const { listId } = useParams();
  const navigate = useNavigate();

  const [ listName, setListName ] = useState("");
  const [ listAuthor, setListAuthor ] = useState("");
  const [ tasks, setTasks ] = useState<Task[]>([]);

  useEffect(() => {
    // TODO: uncomment
    // axios.get<ListResponse>(`/lists/${listId}`)
    //     .then(response => {
    //         setListName(response.data.name)
    //         setListAuthor(response.data.author)
    //         setTasks(response.data.tasks);
    //     })
    //     .catch(() => {
    //       console.log("Error fetching list");
    //     });

    // TODO: placeholders, remove
    setListName("List name")
    setListAuthor("username")
    setTasks(
        [
          {id: 1, name: "task1", isComplete: false},
          {id: 2, name: "task2", isComplete: true}
        ]
    );
  }, [listId, navigate]);

  if (tasks === undefined) {
    return <h2>404 Not Found</h2>;
  }

  function toggleComplete(taskId: number) {
    const newVal = !tasks.find(task => task.id === taskId)?.isComplete;

    // TODO: remove
    setTasks(prevTasks =>
        prevTasks.map(task =>
            task.id === taskId ? {...task, isComplete: newVal} : task
        )
    );

    // TODO: uncomment
    // axios.put(`/lists/${listId}/task`, {
    //   id: taskId,
    //   name: tasks.find(task => task.id === taskId)?.name,
    //   isComplete: newVal,
    // })
    // // update the UI
    // .then(() => {
    //   setTasks(prevTasks =>
    //       prevTasks.map(task =>
    //             task.id === taskId ? {...task, isComplete: newVal} : task
    //       ));
    //   })
    // .catch(error => {
    //   console.error("Failed to update task", error);
    // });
  }

  return (
      <>
        <div className={styles.topMenu}>
          <a href="/home">&lt; Back to All Lists</a>
        </div>

        <div className={styles.listContainer}>
          <div className={styles.header}>
            <h2>{listName}</h2>
            <h3>by {listAuthor}</h3>
          </div>
          <div className={styles.listItems}>
            {tasks.map(task => (
                <div key={task.id} className={styles.listItem}>
                  <input
                      type="checkbox"
                      checked={task.isComplete}
                      onChange={() => toggleComplete(task.id)}
                  />
                  <span className={task.isComplete ? styles.completed : ""}>
                    {task.name}
                  </span>
                  <button>Edit</button>
                  <button className={styles.deleteButton}>Delete</button>
                </div>
            ))}
          </div>
          <button className="add-task">+ Add a task</button>
        </div>
      </>
  )

}

export default List;
