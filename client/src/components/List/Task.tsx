import {useState} from "react";
import styles from "./Task.module.scss";
import axios from "axios";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {faPencil, faTrash, faCheck} from "@fortawesome/free-solid-svg-icons";

interface TaskProps {
  id: number;
  name: string;
  isComplete: boolean;
  deleteTask: (taskId: number) => void;
}

function Task(props: TaskProps) {

  const [ editing, setEditing ] = useState(false);
  const [ textInput, setTextInput ] = useState(props.name);
  const [ isComplete, setIsComplete ] = useState(props.isComplete);
  const [ name, setName ] = useState(props.name);

  // TODO: for both calls below - exclude id? different endpoint? patch?

  function toggleComplete() {
    // TODO: for testing, remove
    setIsComplete(isComplete => !isComplete)

    // TODO: uncomment
    // axios.put(`/lists/${props.id}/task`, {
    //   id: props.id,
    //   name: props.name,
    //   isComplete: !isComplete,
    // })
    // // update the UI
    // .then(() => {
    //   setIsComplete(isComplete => !isComplete)
    // })
    // .catch(error => {
    //   console.error("Failed to update task", error);
    // });
  }

  function handleSave() {
    // TODO: for testing, remove
    setName(textInput);

    // TODO: uncomment
    // axios.put(`/lists/${props.id}/task`, {
    //   id: props.id,
    //   name: textInput,
    //   isComplete: isComplete,
    // })
    // // update the UI
    // .then(() => {
    //   setName(textInput);
    // })
    // .catch(error => {
    //   console.error("Failed to update task", error);
    // });

    setEditing(false);
  }

  return (
    <div key={props.id} className={styles.listItem}>
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
                onClick={() => props.deleteTask(props.id)}>
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
                onClick={() => props.deleteTask(props.id)}>
              <FontAwesomeIcon icon={faTrash} />
            </button>
          </div>
        </>
      )}

    </div>
  )
}

export default Task;
