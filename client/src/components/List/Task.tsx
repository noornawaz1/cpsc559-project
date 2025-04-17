import {useState} from "react";
import styles from "./Task.module.scss";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {faPencil, faTrash, faCheck} from "@fortawesome/free-solid-svg-icons";
import api from "../../services/api.ts";

interface TaskProps {
  listId: string;
  itemId: number;
  name: string;
  completed: boolean;
  deleteTask: (taskId: number) => void;
}

function Task(props: TaskProps) {

  const [ editing, setEditing ] = useState(false);
  const [ textInput, setTextInput ] = useState(props.name);
  const [ isComplete, setIsComplete ] = useState(props.completed);
  const [ name, setName ] = useState(props.name);

  function toggleComplete() {
    api.put(`/todolists/${props.listId}/items/${props.itemId}`, {
      title: props.name,
      completed: !isComplete,
      todoList: props.listId
    })
    // update the UI
    .then(() => {
      setIsComplete(isComplete => !isComplete)
    })
    .catch(error => {
      console.error("Failed to update task", error);
    });
  }

  function handleSave() {
    api.put(`/todolists/${props.listId}/items/${props.itemId}`, {
      title: textInput,
      completed: isComplete,
      todoList: props.listId
    })
    // update the UI
    .then(() => {
      setName(textInput);
    })
    .catch(error => {
      console.error("Failed to update task", error);
    });

    setEditing(false);
  }

  return (
    <div key={props.itemId} className={styles.listItem}>
      <input
          type="checkbox"
          checked={isComplete}
          onChange={toggleComplete}
          className={styles.checkBox}
      />
      {editing ? (
        <>
          <input
            value = {textInput}
            onChange={(e) => setTextInput(e.target.value)}
            className={styles.taskName}
          />
          <div className={styles.end}>
            <button
                className={styles.saveButton + " " + styles.icon}
                onClick={handleSave}>
              <FontAwesomeIcon icon={faCheck}/>
            </button>
            <button
                className={styles.deleteButton + " " + styles.icon}
                onClick={() => props.deleteTask(props.itemId)}>
              <FontAwesomeIcon icon={faTrash} />
            </button>
          </div>
        </>
      ) : (
        <>
          <span className={(isComplete ? styles.completed : "") + " " + styles.taskName}>{name}</span>
          <div className={styles.end}>
            <button
                className={styles.editButton + " " + styles.icon}
                onClick={() => setEditing(true)}>
              <FontAwesomeIcon icon={faPencil}/>
            </button>
            <button
                className={styles.deleteButton + " " + styles.icon}
                onClick={() => props.deleteTask(props.itemId)}>
              <FontAwesomeIcon icon={faTrash} />
            </button>
          </div>
        </>
      )}

    </div>
  )
}

export default Task;
