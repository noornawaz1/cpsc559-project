import {useEffect, useRef, useState} from "react";
import { useParams } from "react-router-dom";
import styles from "./List.module.scss";
import Task from "../../components/List/Task.tsx";
import axios from "axios";
import AddTaskModal from "../../components/List/AddTaskModal.tsx";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus } from "@fortawesome/free-solid-svg-icons";

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

  const modal = useRef<HTMLDialogElement>(null);

  const { listId } = useParams();

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
  }, [listId]);

  if (tasks === undefined) {
    return <h2>404 Not Found</h2>;
  }

  function deleteTask(taskId: number) {

    // TODO: remove
    setTasks(prevTasks =>
      (prevTasks.filter(task => task.id !== taskId))
    );

    // TODO: uncomment
    // axios.delete(`/lists/${listId}/task/${taskId}`)
    // .then(() => {
    //   setTasks(prevTasks =>
    //       (prevTasks.filter(task => task.id !== taskId))
    //   );
    // })
    // .catch(() => {
    //   console.log("Error deleting task");
    // });
    // console.log("deleted task")
  }

  function addTask(taskName: string) {
    axios.put(`/lists/${listId}/task`, {
          name: taskName,
          isComplete: false,
        })
        .then(() => {
          console.log(`created new task "${taskName}"`)
        })
        .catch(error => {
          console.error("Failed to add task", error);
        });
    modal.current?.close()
  }

  function showModal() {
    modal.current?.showModal()
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
              <Task
                key = {task.id}
                id = {task.id}
                name = {task.name}
                isComplete={task.isComplete}
                deleteTask={(id) => deleteTask(id)}
              />
            ))}
          </div>
          <button className={styles.addBtn} onClick={showModal}>
            <FontAwesomeIcon icon={faPlus} className={styles.iconSpacing} />
            Add a Task
          </button>
        </div>
        <AddTaskModal ref={modal} addTask={addTask}/>
      </>
  )
}

export default List;
