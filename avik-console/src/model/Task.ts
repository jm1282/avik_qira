import { jsonObject, jsonMember, jsonArrayMember } from 'typedjson'

import { isNaN } from 'lodash'

/**
 * The task module. It is a part of the round module.
 */
@jsonObject
export default class Task {
  @jsonMember(String)
  id?: string

  @jsonMember(String)
  label?: string

  @jsonMember(Number)
  type?: TaskType

  @jsonMember(String)
  atm?: string

  @jsonArrayMember(String)
  projects?: string[]
}

export enum TaskType {
  REVIEW = 1,
  CXD = 10,
  BASE = 6,
  TRAINING = 7,
}

export const getTaskTypeKeys = (): string[] => Object.keys(TaskType).filter(key => !/\d+/.test(key))

export const getTaskTypeValues = (): (TaskType | string)[] =>
  Object.values(TaskType).filter(value => !isNaN(Number(value)))

export const getTaskTypeDict = (): { [key: string]: TaskType } => {
  const result: { [key: string]: TaskType } = {}
  getTaskTypeKeys().forEach(key => (result[key] = TaskType[key as keyof typeof TaskType]))
  return result
}

/**
 * Convert an object to a Task
 *
 * @param payload The original object
 * @returns The Task
 */
export const convertTaskFromObject = (payload: Partial<Task>): Task => {
  const instance = new Task()
  instance.id = payload.id
  instance.label = payload.label
  instance.type = payload.type
  instance.atm = payload.atm
  instance.projects = payload.projects
  return instance
}
