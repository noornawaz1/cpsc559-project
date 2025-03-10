import {Ref, useState} from "react";
import styles from "./AddTaskModal.module.scss";

interface AddTaskModalProps {
  ref: Ref<HTMLDialogElement>;
  addTask: (taskName: string) => void;
}

function AddTaskModal(props: AddTaskModalProps) {
  const [ input, setInput ] = useState("");

  return (
      <dialog ref={props.ref} className={styles.addTaskDialog}>
        <h3>Enter task name:</h3>
        <form>
          <input
            placeholder={"Task name"}
            value={input}
            onChange={(e) => setInput(e.target.value)}
          />
          <button onClick={() => props.addTask(input)} type="button">Create task</button>
        </form>
        <form method="dialog">
          <button>Cancel</button>
        </form>
      </dialog>
  );
}

export default AddTaskModal;
